package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
	private Camera camera;
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("CameraService");
        this.camera = camera;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
    	 // Subscribe to TickBroadcast to send DetectObjectsEvents at the appropriate time
        subscribeBroadcast(TickBroadcast.class, tick -> {
        	//Search for StampedDetectedObjects fit to tick
        	StampedDetectedObjects SDO = camera.getDetectedObjectsForTick(tick.getCurrentTick());
        	//Search for ERROR object and send CrashedBroadcast
        	boolean errorFound = SDO.getDetectedObjects().stream()
                    .anyMatch(obj -> "ERROR".equals(obj.getId()));
        	if(errorFound) {
        		sendBroadcast(new CrashedBroadcast("Camera_"+camera.getId(), "Camera detected an error."));
                terminate();
                return;
        	}
        	
        	// Send DetectObjectsEvent for valid objects
            DetectObjectsEvent event = new DetectObjectsEvent(SDO);
            sendEvent(event);
            
        	//update statistical folder
            SDO.getDetectedObjects().forEach(DetectedObject ->
            StatisticalFolder.getInstance().incrementDetectedObjects());
        	
        });
        	/*old version
        	 * 
            // Check if the current tick matches the camera's frequency and the camera is operational
            if (tick.getCurrentTick() % camera.getFrequency() == 0 && camera.isUp()) {
                // Get detected objects at the current tick and send events for each
                camera.getObjectsList().forEach(detectedObject -> {
                    sendEvent(new DetectObjectsEvent(detectedObject)); // Send DetectObjectsEvent
                    StatisticalFolder.getInstance().incrementDetectedObjects(); // Update statistics
                });
            }
        });
        	*/
        
        // Subscribe to TerminatedBroadcast to terminate the service if needed
        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            terminate();
        });

        // Subscribe to CrashedBroadcast to terminate the service in case of a crash
        subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            terminate();
        });
    }
}
