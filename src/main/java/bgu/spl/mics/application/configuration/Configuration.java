package bgu.spl.mics.application.configuration;

public class Configuration {
    private CamerasConfig Cameras;
    private LiDarWorkersConfig LiDarWorkers;
    private String poseJsonFile;
    private int TickTime;
    private int Duration;

    // Getters and Setters
    public CamerasConfig getCameras() { return Cameras; }
    public void setCameras(CamerasConfig cameras) { Cameras = cameras; }

    public LiDarWorkersConfig getLiDarWorkers() { return LiDarWorkers; }
    public void setLiDarWorkers(LiDarWorkersConfig liDarWorkers) { LiDarWorkers = liDarWorkers; }

    public String getPoseJsonFile() { return poseJsonFile; }
    public void setPoseJsonFile(String poseJsonFile) { this.poseJsonFile = poseJsonFile; }

    public int getTickTime() { return TickTime; }
    public void setTickTime(int tickTime) { TickTime = tickTime; }

    public int getDuration() { return Duration; }
    public void setDuration(int duration) { Duration = duration; }
        
}

