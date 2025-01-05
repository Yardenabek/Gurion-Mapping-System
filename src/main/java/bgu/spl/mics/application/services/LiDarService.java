package bgu.spl.mics.application.services;

import java.util.ArrayList;

import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
	private final LiDarWorkerTracker liDarTracker;
	private int currentTick;
    /**
     * Constructor for LiDarService.
     *
     * @param liDarTracker The LiDAR tracker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker liDarTracker) {
        super("LidarService");
        this.liDarTracker = liDarTracker;
        this.currentTick = 0; // Initialize the tick counter    
    }
   


    
    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callback for processing data.
     */
    @Override
    protected void initialize() {
    	 // Subscribe to TickBroadcast to keep track of the current tick
        subscribeBroadcast(TickBroadcast.class, tick -> {
            currentTick = tick.getCurrentTick();
            if (currentTick % liDarTracker.getFrequency() == 0 && liDarTracker.getStatus() == STATUS.UP) {
                System.out.println(getName() + " is ready to process events at tick " + currentTick);
            }
        });

        // Subscribe to DetectObjectsEvent to process detected objects
        subscribeEvent(DetectObjectsEvent.class, detectEvent -> {
            List<TrackedObject> trackedObjects = new ArrayList<>();
            for (DetectedObject object : detectEvent.getStampedDetectedObjects().getDetectedObjects()) {
                StatisticalFolder.getInstance().incrementDetectedObjects();
            	// Retrieve the tracked object from the tracker
                TrackedObject trackedObject = liDarTracker.getTrackedObjectById(object.getId(),object.getDescription());
                if (trackedObject != null) {
                    trackedObjects.add(trackedObject);
                }
            }

            // Send TrackedObjectsEvent to FusionSLAM if there are any tracked objects
            if (!trackedObjects.isEmpty()) {
                sendEvent(new TrackedObjectsEvent(trackedObjects));
                trackedObjects.forEach(obj -> {
                    liDarTracker.addTrackedObject(obj); // Update the tracker
                    StatisticalFolder.getInstance().incrementTrackedObjects(); // Increment tracked objects count in StatisticalFolder
                });

               // System.out.println(getName() + " sent TrackedObjectsEvent with " + trackedObjects.size() + " objects.");
            }
            
            complete(detectEvent, true); // Mark the DetectObjectsEvent as completed
        });
        subscribeBroadcast(TerminatedBroadcast.class,terminated->{
        	terminate();
        });
        
        subscribeBroadcast(CrashedBroadcast.class,crashed->{
        	terminate();
        });
    }
}
    
