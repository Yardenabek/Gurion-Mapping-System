package bgu.spl.mics.application.objects;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private int systemRuntime;
    private int numDetectedObjects;
    private int numTrackedObjects;
    private int numLandmarks;
    private final Object lockSystem;
    private final Object lockDetected;
    private final Object lockTracked;
    private final Object lockLandmarks;

    
    public StatisticalFolder() {
    	this.systemRuntime = 0;
    	this.numDetectedObjects = 0;
    	this.numTrackedObjects = 0;
    	this.numLandmarks = 0;
    	this.lockSystem = new Object();
    	this.lockDetected = new Object();
    	this.lockTracked = new Object();
    	this.lockLandmarks = new Object();
    }
    public int getsystemRuntime() {
    	synchronized(lockSystem) {
    	return systemRuntime;
    	}
    }
    public int getDetectedObjects() {
    	synchronized(lockDetected) {
    		return numDetectedObjects;
    	}
    }
    public int getTrackedObjects() {
    	synchronized(lockTracked) {
        	return numTrackedObjects;
    	}
    }
    public int getLandmarks() {
    	synchronized(lockLandmarks) {
        	return numLandmarks;
    	}
    }
    public void incrementSystemRuntime() {
    	synchronized(lockSystem) {
        	systemRuntime++;
    	}
    }
    public void incrementDetectedObjects() {
    	synchronized(lockDetected) {
        	numDetectedObjects++;
    	}
    }
    public void incrementTrackedObjects() {
    	synchronized(lockTracked) {
        	numTrackedObjects++;
    	}
    }
    public void incrementLandmarks() {
    	synchronized(lockLandmarks) {
        	numLandmarks++;
    	}
    }
}
