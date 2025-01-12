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
    private boolean isActive; // Indicates if the camera is operational
    
    public Camera(int id,int frequency,List <StampedDetectedObjects> detectedObjectsList){
    	this.id = id;
    	this.frequency = frequency;
    	this.status = STATUS.UP;
    	this.detectedObjectsList = detectedObjectsList;
    	this.isActive = true; // Camera starts as active by default
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
    public boolean isUp() {
        return isActive;
    }
    public void deactivate() {
        isActive = false;
        status = STATUS.DOWN; // Update the status to DOWN
    }
    public void activate() {
        isActive = true;
        status = STATUS.UP; // Update the status to UP
        System.out.println("Camera status set to DOWN.");

    }
    public StampedDetectedObjects getDetectedObjectsForTick(int tick) {
        System.out.println("getDetectedObjectsForTick called for tick: " + tick);
        int targetTick = tick + this.frequency;
        System.out.println("Target tick: " + targetTick);

        // הדפסת כל האובייקטים ברשימה
        for (StampedDetectedObjects obj : detectedObjectsList) {
            System.out.println("Checking detected object with time: " + obj.getTime());
        }

        // ניסיון למצוא את האובייקט המתאים
        StampedDetectedObjects result = detectedObjectsList.stream()
            .filter(obj -> obj.getTime() == targetTick)
            .findFirst()
            .orElse(null);

        if (result == null) {
            System.out.println("No matching detected objects for target tick: " + targetTick);
        } else {
            System.out.println("Found detected objects for target tick: " + targetTick);
        }

        return result;
    }

}
