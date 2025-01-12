package bgu.spl.tests;

import bgu.spl.mics.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MessageBusTest {

    private MessageBusImpl messageBus;
    private MicroService serviceA;
    private MicroService serviceB;

    @BeforeEach
    public void setUp() {
        // Reset the Singleton instance of MessageBusImpl
        MessageBusImpl.reset();
        messageBus = MessageBusImpl.getInstance();

        // Initialize two MicroServices
        serviceA = new MicroService("ServiceA") {
            @Override
            protected void initialize() {
            	subscribeEvent(TestEvent.class,event ->{
            		System.out.println("event:"+event.getMsg());
            	});
            	subscribeBroadcast(TestBroadcast.class,broadcast ->{
            		System.out.println("broadcast:"+broadcast.getMsg());
            	});
            }
        };

        serviceB = new MicroService("ServiceB") {
            @Override
            protected void initialize() {
            	subscribeEvent(TestEvent.class,event ->{
            		System.out.println("event:"+event.getMsg());
            	});
            	subscribeBroadcast(TestBroadcast.class,broadcast ->{
            		System.out.println("broadcast:"+broadcast.getMsg());
            	});
            }
        };

        // Register the MicroServices
        messageBus.register(serviceA);
        messageBus.register(serviceB);
    }

    @AfterEach
    public void tearDown() {
        // Reset the Singleton instance of MessageBusImpl after each test
        MessageBusImpl.reset();
    }

    @Test
    public void testRegister() {
        MicroService serviceC = new MicroService("ServiceC") {
            @Override
            protected void initialize() {subscribeEvent(TestEvent.class,event ->{
        		System.out.println("event:"+event.getMsg());
        	});
        	subscribeBroadcast(TestBroadcast.class,broadcast ->{
        		System.out.println("broadcast:"+broadcast.getMsg());
        	});}
        };

        // Register a new service
        messageBus.register(serviceC);

        // Ensure the service can receive messages
        messageBus.subscribeBroadcast(TestBroadcast.class, serviceC);
        messageBus.sendBroadcast(new TestBroadcast("test register"));

        try {
            assertEquals(TestBroadcast.class, messageBus.awaitMessage(serviceC).getClass());
        } catch (InterruptedException e) {
            fail("ServiceC failed to receive broadcast");
        }
    }

    @Test
    public void testUnregister() {
        // Unregister ServiceA and verify that it is no longer available
        messageBus.unregister(serviceA);

        // Verify that ServiceA cannot receive messages
        assertThrows(IllegalStateException.class, () -> messageBus.awaitMessage(serviceA));
    }

    @Test
    public void testSubscribeEvent() {
        // Test valid event subscription and message receipt
        messageBus.subscribeEvent(TestEvent.class, serviceA);

        Event<String> event = new TestEvent("test subscribe event");
        Future<String> future = messageBus.sendEvent(event);

        assertNotNull(future); // Verify that a future is returned

        try {
            assertEquals(event, messageBus.awaitMessage(serviceA));
        } catch (InterruptedException e) {
            fail("ServiceA failed to receive the event");
        }

        // Test sending an event without subscribers
        Event<String> event2 = new TestEvent("Test sending an event without subscribers");
        Future<String> future2 = messageBus.sendEvent(event2);
        assertNull(future2); // No one subscribed to handle the event
    }

    @Test
    public void testSubscribeBroadcast() {
        // Test valid broadcast subscription
        messageBus.subscribeBroadcast(TestBroadcast.class, serviceB);

        Broadcast broadcast = new TestBroadcast("Test valid broadcast subscription");
        messageBus.sendBroadcast(broadcast);

        try {
            assertEquals(broadcast, messageBus.awaitMessage(serviceB));
        } catch (InterruptedException e) {
            fail("ServiceB failed to receive the broadcast");
        }

        // Test sending a broadcast without subscribers
        Broadcast broadcast2 = new TestBroadcast("Test sending a broadcast without subscribers");
        messageBus.sendBroadcast(broadcast2);

        // ServiceA and ServiceB should not receive this broadcast
        assertThrows(InterruptedException.class, () -> {
            // Simulate waiting for ServiceA and verify no message is received
            synchronized (messageBus) {
                messageBus.awaitMessage(serviceA);
            }
        });

        assertThrows(InterruptedException.class, () -> {
            // Simulate waiting for ServiceB and verify no message is received
            synchronized (messageBus) {
                messageBus.awaitMessage(serviceB);
            }
        });
    }


    @Test
    public void testSendEventRoundRobin() {
        // Test round-robin distribution of events
        messageBus.subscribeEvent(TestEvent.class, serviceA);
        messageBus.subscribeEvent(TestEvent.class, serviceB);

        Event<String> event1 = new TestEvent("Test round-robin distribution of events 1");
        Event<String> event2 = new TestEvent("Test round-robin distribution of events 2");

        messageBus.sendEvent(event1);
        messageBus.sendEvent(event2);

        try {
            assertEquals(event1, messageBus.awaitMessage(serviceA));
            assertEquals(event2, messageBus.awaitMessage(serviceB));
        } catch (InterruptedException e) {
            fail("Round-robin event distribution failed");
        }
    }

    @Test
    public void testCompleteEvent() {
        // Test completing an event and resolving its future
        messageBus.subscribeEvent(TestEvent.class, serviceA);

        Event<String> event = new TestEvent("Test completing an event and resolving its future");
        Future<String> future = messageBus.sendEvent(event);

        // Complete the event
        messageBus.complete(event, "Success");

        assertTrue(future.isDone());
        assertEquals("Success", future.get());
    }

    @Test
    public void testAwaitMessageThrowsExceptionForUnregisteredService() {
        MicroService unregisteredService = new MicroService("UnregisteredService") {
            @Override
            protected void initialize() {}
        };

        // Attempt to call awaitMessage on an unregistered service
        assertThrows(IllegalStateException.class, () -> messageBus.awaitMessage(unregisteredService));
    }
}

class TestBroadcast implements Broadcast {
	String Msg;
	public TestBroadcast(String msg) {
		this.Msg = msg;
	}
	public String getMsg() {
		return Msg;
	}
}
class TestEvent implements Event<String> {
	String Msg;
	public TestEvent(String msg) {
		this.Msg = msg;
	}
	public String getMsg() {
		return Msg;
	}
}

