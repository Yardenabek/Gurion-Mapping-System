package bgu.spl.tests;

import bgu.spl.mics.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MessageBusTest {

    private MessageBusImpl messageBus;
    private TestMicroService serviceA;
    private TestMicroService serviceB;
    private Thread threadA;
    private Thread threadB;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        // Reset the Singleton instance of MessageBusImpl
        MessageBusImpl.reset();
        messageBus = MessageBusImpl.getInstance();

        // Initialize two MicroServices
        serviceA = new TestMicroService("ServiceA");
        serviceB = new TestMicroService("ServiceB");
            
        // run threads of microservices
        threadA = new Thread(serviceA);
        threadB = new Thread(serviceB);
        threadA.start();
        threadB.start();
        
        //redirect output stream
        System.setOut(new PrintStream(outputStream));

    }

    @AfterEach
    public void tearDown() {
        // Reset the Singleton instance of MessageBusImpl after each test
        MessageBusImpl.reset();
        serviceA.forceTerminate();
        serviceB.forceTerminate();
        
        //restore original output
        System.setOut(originalOut);
    }

    @Test
    public void testRegisterAndSendEvent() throws InterruptedException {
        // Ensure the service can receive messages
        Future<Boolean> future = messageBus.sendEvent(new TestEvent("test Register"));
        assertEquals(future.get(),true);
    }

    @Test
    public void testUnregister() {
        // Unregister ServiceA and verify that it is no longer available
        messageBus.unregister(serviceA);
        // Verify that ServiceA cannot receive messages
        assertThrows(IllegalStateException.class, () -> messageBus.awaitMessage(serviceA));
    }

    @Test
    public void testSubscribeEvent() throws InterruptedException {
        // Test valid event subscription and message receipt
        TestEvent event = new TestEvent("test subscribe event");
        Future<Boolean> future = messageBus.sendEvent(event);
        assertNotNull(future); // Verify that a future is returned

        assertEquals(future.get(),true);

        // Test sending an event without subscribers
        TestEvent2 event2 = new TestEvent2();
        Future<Boolean> future2 = messageBus.sendEvent(event2);
        assertNull(future2); // No one subscribed to handle the event
    }

    @Test
    public void testSubscribeBroadcast() throws InterruptedException {

        TestBroadcast broadcast = new TestBroadcast("Test valid broadcast subscription");
        messageBus.sendBroadcast(broadcast);
        String lastPrinted = outputStream.toString().trim();
        assertEquals(true,(lastPrinted.isEmpty()));
       
    }


    @Test
    public void testSendEventRoundRobin() throws InterruptedException {
        // Test round-robin distribution of events
        TestEvent event1 = new TestEvent("Test round-robin distribution of events 1");
        TestEvent event2 = new TestEvent("Test round-robin distribution of events 2");

        Future<Boolean> future1 = messageBus.sendEvent(event1);
        Future<Boolean> future2 = messageBus.sendEvent(event2);

        assertEquals(future1.get(), true);
		assertEquals(future2.get(), true);
    }

    @Test
    public void testCompleteEvent() {
        // Test completing an event and resolving its future
        messageBus.subscribeEvent(TestEvent.class, serviceA);

        TestEvent event = new TestEvent("Test completing an event and resolving its future");
        Future<Boolean> future = messageBus.sendEvent(event);

        assertEquals(true, future.get());
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
class TestMicroService extends MicroService{
	public TestMicroService(String name){
		super(name);
	}
	@Override
    protected void initialize() {
    	subscribeEvent(TestEvent.class,event ->{
    		System.out.println("event:"+event.getMsg());
    		complete(event,true);
    	});
    	subscribeBroadcast(TestBroadcast.class,broadcast ->{
    		System.out.println("broadcast:"+broadcast.getMsg());
    	});
    }
    public void forceTerminate() {
    	this.terminate();
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
class TestEvent implements Event<Boolean> {
	String Msg;
	public TestEvent(String msg) {
		this.Msg = msg;
	}
	public String getMsg() {
		return Msg;
	}
}
class TestEvent2 implements Event<Boolean> {}

