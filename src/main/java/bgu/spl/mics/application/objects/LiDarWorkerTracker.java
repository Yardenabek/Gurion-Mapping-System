package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {

    private final int id;
    private final int frequency;
    private STATUS status;
    private List <TrackedObject> lastTrackedObjects;
    private String DataBasePath;
    
    public LiDarWorkerTracker(int id,int frequency,String DataBasePath) {
    	this.id = id;
    	this.frequency = frequency;
    	this.status = STATUS.UP;
    	this.lastTrackedObjects = new ArrayList<>();
    	this.DataBasePath = DataBasePath;
    }
    public int getId() {
    	return id;
    }
    public int getFrequency() {
    	return frequency;
    }
    public STATUS getStatus() {
    	return status;
    }
    public void setStatus(STATUS status) {
    	this.status = status;
    }
    public boolean addTrackedObject(TrackedObject object) {
    	return lastTrackedObjects.add(object);
    }
	public TrackedObject getTrackedObjectById(String id,String description) {
		synchronized (LiDarDataBase.getInstance(DataBasePath)) {
			StampedCloudPoints scp = LiDarDataBase.getInstance(DataBasePath).getStampedCloudPointsById(id);
			if(scp!=null) {
				return new TrackedObject(scp.getId(),scp.getTime(),description,scp.getCloudPoints());
			}
			return null;
        }
	}
    
}
