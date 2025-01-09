package bgu.spl.tests;

import bgu.spl.mics.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

public class MessageBusTest {

    private MessageBusImpl messageBus;
    private MicroService serviceA;
    private MicroService serviceB;

    @Before
    public void setUp() {
        // Reset the Singleton instance of MessageBusImpl
        MessageBusImpl.reset();
        messageBus = MessageBusImpl.getInstance();

        // Initialize two MicroServices
        serviceA = new MicroService("ServiceA") {
            @Override
            protected void initialize() {}
        };

        serviceB = new MicroService("ServiceB") {
            @Override
            protected void initialize() {}
        };

        // Register the MicroServices
        messageBus.register(serviceA);
        messageBus.register(serviceB);
    }

    @After
    public void tearDown() {
        // Reset the Singleton instance of MessageBusImpl after each test
        MessageBusImpl.reset();
    }

    @Test
    public void testRegister() {
        MicroService serviceC = new MicroService("ServiceC") {
            @Override
            protected void initialize() {}
        };

        // Register a new service
        messageBus.register(serviceC);

        // Ensure the service can receive messages
        messageBus.subscribeBroadcast(TestBroadcast.class, serviceC);
        messageBus.sendBroadcast(new TestBroadcast());

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
        class TestEvent implements Event<String> {}
        messageBus.subscribeEvent(TestEvent.class, serviceA);

        Event<String> event = new TestEvent();
        Future<String> future = messageBus.sendEvent(event);

        assertNotNull(future); // Verify that a future is returned

        try {
            assertEquals(event, messageBus.awaitMessage(serviceA));
        } catch (InterruptedException e) {
            fail("ServiceA failed to receive the event");
        }

        // Test sending an event without subscribers
        Event<String> event2 = new TestEvent();
        Future<String> future2 = messageBus.sendEvent(event2);
        assertNull(future2); // No one subscribed to handle the event
    }

    @Test
    public void testSubscribeBroadcast() {
        // Test valid broadcast subscription
        class TestBroadcast implements Broadcast {}
        messageBus.subscribeBroadcast(TestBroadcast.class, serviceB);

        Broadcast broadcast = new TestBroadcast();
        messageBus.sendBroadcast(broadcast);

        try {
            assertEquals(broadcast, messageBus.awaitMessage(serviceB));
        } catch (InterruptedException e) {
            fail("ServiceB failed to receive the broadcast");
        }

        // Test sending a broadcast without subscribers
        class TestBroadcast2 implements Broadcast {}
        Broadcast broadcast2 = new TestBroadcast2();
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
        class TestEvent implements Event<String> {}
        messageBus.subscribeEvent(TestEvent.class, serviceA);
        messageBus.subscribeEvent(TestEvent.class, serviceB);

        Event<String> event1 = new TestEvent();
        Event<String> event2 = new TestEvent();

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
        class TestEvent implements Event<String> {}
        messageBus.subscribeEvent(TestEvent.class, serviceA);

        Event<String> event = new TestEvent();
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

class TestBroadcast implements Broadcast {}
