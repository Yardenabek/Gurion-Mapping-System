package bgu.spl.mics.application.objects;

import java.util.List;

public class Configuration {
    private List<Camera> Cameras; 
    private List<LiDarWorkerTracker> Lidars;
    private String poseJsonFile;
    private int TickTime;
    private int Duration;

    // Getters and Setters
    public List<Camera> getCameras() {
        return Cameras;
    }

    public void setCameras(List<Camera> cameras) {
        this.Cameras = cameras;
    }

    public List<LiDarWorkerTracker> getLidars() {
        return Lidars;
    }

    public void setLidars(List<LiDarWorkerTracker> lidars) {
        this.Lidars = lidars;
    }

    public String getPoseJsonFile() {
        return poseJsonFile;
    }

    public void setPoseJsonFile(String poseJsonFile) {
        this.poseJsonFile = poseJsonFile;
    }

    public int getTickTime() {
        return TickTime;
    }

    public void setTickTime(int tickTime) {
        this.TickTime = tickTime;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        this.Duration = duration;
    }
}

