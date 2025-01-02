package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
	private List<LandMark> Landmarks;
    private List<Pose> Poses;
    private int currentTick;
    private Pose lastPose = null;
    
    private FusionSlam() {
    	this.Landmarks = new ArrayList<LandMark>();
    	this.Poses = new ArrayList<Pose>();
    	this.currentTick = 0;
    }
    // Singleton instance holder
    private static class FusionSlamHolder {
        private static final FusionSlam Instance = new FusionSlam();
    }
    public FusionSlam getInstance() {
    	return FusionSlamHolder.Instance;
    }
    public List<LandMark> getLandMarks(){
    	return Landmarks;
    }
    public List<Pose> getPoses(){
    	return Poses;
    }
    public Pose getPoseAtTime(int time) {
    	for(Pose pose : Poses) {
    		if(pose.getTime() == time) {
    			return pose;
    		}
    	}
    	return null;
    }
    public Pose getCurrentPose() {
    	return lastPose;
    }
    public boolean addLandMark(LandMark landmark) {
    	return Landmarks.add(landmark);
    }
    public boolean addPose(Pose pose) {
    	lastPose = pose;
    	return Poses.add(pose);
    }
    public void updateTick(int tick) {
    	currentTick = tick;
    }
    public int getCurrentTick() {
    	return currentTick;
    }
	public LandMark getLandmark(String id) {
		for(LandMark landmark : Landmarks) {
			if(landmark.getId().equals(id)) {
				return landmark;
			}
		}
		return null;
	}
	public List<CloudPoint> transformToGlobalFrame(List<CloudPoint> coordinates, Pose currentPose) {
		List<CloudPoint> globalCoordinates = new ArrayList<>();

        double xRobot = currentPose.getX();
        double yRobot = currentPose.getY();
        double yawRobot = Math.toRadians(currentPose.getYaw()); 

        double cosYaw = Math.cos(yawRobot);
        double sinYaw = Math.sin(yawRobot);

        for (CloudPoint localPoint : coordinates) {
            double xGlobal = cosYaw * localPoint.getX() - sinYaw * localPoint.getY() + xRobot;
            double yGlobal = sinYaw * localPoint.getX() + cosYaw * localPoint.getY() + yRobot;
            globalCoordinates.add(new CloudPoint(xGlobal, yGlobal));
        }

        return globalCoordinates;
	}
	public void updateLandmark(String id, List<CloudPoint> transformedPoints) {
		 for (LandMark landmark : Landmarks) {
			 if (landmark.getId().equals(id)) {
                List<CloudPoint> existingCoordinates = landmark.getCoordinates();
                List<CloudPoint> updatedCoordinates = new ArrayList<>();

                for (int i = 0; i < Math.min(existingCoordinates.size(), transformedPoints.size()); i++) {
                    double avgX = (existingCoordinates.get(i).getX() + transformedPoints.get(i).getX()) / 2;
                    double avgY = (existingCoordinates.get(i).getY() + transformedPoints.get(i).getY()) / 2;
                    updatedCoordinates.add(new CloudPoint(avgX, avgY));
                }

                landmark.setCoordinates(updatedCoordinates);
            }
		 }
	}
}
