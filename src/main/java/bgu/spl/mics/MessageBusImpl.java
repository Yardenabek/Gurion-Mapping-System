package bgu.spl.mics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private final ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServiceQueues;
    private final ConcurrentHashMap<Class<? extends Event<?>>, List<MicroService>> eventSubscribers;
    private final ConcurrentHashMap<Class<? extends Broadcast>, List<MicroService>> broadcastSubscribers;
    private final ConcurrentHashMap<Class<? extends Event<?>>, Integer> eventRoundRobinIndex;
    private final ConcurrentHashMap<Event<?>, Future<?>> eventFutures;
    private final Object lock;
    private static MessageBusImpl instance = null;


    private MessageBusImpl() {
        microServiceQueues = new ConcurrentHashMap<>();
        eventSubscribers = new ConcurrentHashMap<>();
        broadcastSubscribers = new ConcurrentHashMap<>();
        eventRoundRobinIndex = new ConcurrentHashMap<>();
        eventFutures = new ConcurrentHashMap<>();

        lock = new Object();
    }
    
    public static synchronized MessageBusImpl getInstance() {
        if (instance == null) {
            instance = new MessageBusImpl();
        }
        return instance;
    }

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (lock) {
	        eventSubscribers.putIfAbsent(type, new ArrayList<>());
	        eventSubscribers.get(type).add(m);
	    }
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (lock) {
	        broadcastSubscribers.putIfAbsent(type, new ArrayList<>());
	        broadcastSubscribers.get(type).add(m);
	    }
	}


	@Override
	public <T> void complete(Event<T> e, T result) {
		synchronized (lock) { 
			@SuppressWarnings("unchecked")
	        Future<T> future = (Future<T>) eventFutures.get(e);
	        if (future != null) {
	            future.resolve(result); // Resolve the Future
	            eventFutures.remove(e); // Cleanup
	        }
	    }
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		 synchronized (lock) {
			 // מקבל את רשימת השירותים שנרשמו לשידור מסוג b
			 List<MicroService> subscribers = broadcastSubscribers.get(b.getClass());
			 if (subscribers != null) {
				 for (MicroService m : subscribers) {
					 BlockingQueue<Message> queue = microServiceQueues.get(m);
					 if (queue != null) {
						 queue.offer(b); // מוסיף את השידור לתור של השירות
					 }
				 }
			 }
		 }
	}	

	
	@Override
	public <T> Future<T> sendEvent( Event<T> e) {
	    synchronized (lock) {
	        List<MicroService> subscribers = eventSubscribers.get(e.getClass());
	        if (subscribers == null || subscribers.isEmpty()) {
	            return null; // No subscribers for this event type
	        }

	        Future<T> future = new Future<>();
	        eventFutures.put(e, future);

	        // Round-robin logic for dispatching events
	        int index = eventRoundRobinIndex.getOrDefault(e.getClass(), 0);
	        MicroService selectedService = subscribers.get(index);

	        // Update round-robin index
	        eventRoundRobinIndex.put((Class<? extends Event<?>>) e.getClass(), (index + 1) % subscribers.size());

	        // Add the event and its Future to the service's queue
	        BlockingQueue<Message> queue = microServiceQueues.get(selectedService);
	        if (queue != null) {
	            queue.offer(e);
	        }

	        return future;
	    }
	}

	@Override
	public void register(MicroService m) {
		synchronized (lock) {
	        // Check if the microservice is already registered
	        if (!microServiceQueues.containsKey(m)) {
	            // Create a new message queue for the microservice
	            BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
	            // Add the microservice and its queue to the map
	            microServiceQueues.put(m, queue);
	        }
	    }
	}

	@Override
	public void unregister(MicroService m) {
		synchronized (lock) {
	        // Remove the microservice's message queue
	        microServiceQueues.remove(m);

	        // Remove the microservice from all event subscribers
	        for (List<MicroService> subscribers : eventSubscribers.values()) {
	            subscribers.remove(m);
	        }

	        // Remove the microservice from all broadcast subscribers
	        for (List<MicroService> subscribers : broadcastSubscribers.values()) {
	            subscribers.remove(m);
	        }
	    }
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
	    BlockingQueue<Message> queue = microServiceQueues.get(m);

	    if (queue == null) {
	        throw new IllegalStateException("MicroService is not registered with the MessageBus");
	    }

	    try {
	        // Wait indefinitely for a message to become available
	        return queue.take();
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt(); // Restore interrupted status
	        throw new InterruptedException("Thread interrupted while awaiting message for: " + m.getName());
	    }
	}
	
	 // Resets the singleton instance. Intended for testing purposes only.
    public static synchronized void reset() {
        instance = null;
    }

	

}
