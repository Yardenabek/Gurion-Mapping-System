package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick;
    private STATUS status;
    private List<Pose> PoseList;
    
    public GPSIMU(int currentTick,List<Pose> PoseList) {
    	this.currentTick = currentTick;
    	this.status = STATUS.UP;
    	this.PoseList = PoseList;
    }
    public void setTick(int tick) {
    	currentTick = tick;
    }
    public int getTick() {
    	return currentTick;
    }
    public void setStatus(STATUS stat) {
    	status = stat;
    }
    public STATUS getStatus() {
    	return status;
    }
    public boolean addPose(Pose pose) {
    	return PoseList.add(pose);
    }
    public List<Pose> getPoseList(){
    	return PoseList;
    }
}
