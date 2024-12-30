package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
	private final int tickTime; // Duration of each tick in milliseconds
    private final int duration; // Total number of ticks before termination
    private int currentTick;    // Counter for the current tick
    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
    	super("TimeService");
        this.tickTime = TickTime;
        this.duration = Duration;
        this.currentTick = 0;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        // Create a new thread to manage ticks
        new Thread(() -> {
            while (currentTick < duration) {
                currentTick++;
                sendBroadcast(new TickBroadcast(currentTick)); // Send TickBroadcast
                try {
                    Thread.sleep(tickTime); // Wait for the next tick
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            terminate(); // Terminate the TimeService after all ticks are sent
        }).start();
    }
}
