package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private final int id;
    private final int frequency;
    private STATUS status;
    private List <StampedDetectedObjects> detectedObjectsList;
    
    public Camera(int id,int frequency,List <StampedDetectedObjects> detectedObjectsList){
    	this.id = id;
    	this.frequency = frequency;
    	this.status = STATUS.UP;
    	this.detectedObjectsList = detectedObjectsList;
    }
    public void setStatus(STATUS status) {
    	this.status = status;
    }
    public boolean addObject(StampedDetectedObjects object) {
    	return detectedObjectsList.add(object);
    }
    public List<StampedDetectedObjects> getObjectsList(){
    	return detectedObjectsList;
    }
    public int getFrequency() {
    	return frequency;
    }
    public int getId() {
    	return id;
    }
    public STATUS getStatus() {
    	return status;
    }
    
}
