package bgu.spl.mics.application.configuration;

import java.util.List;

public class CamerasConfig {
    private List<CameraConfig> CamerasConfigurations;
    private String camera_datas_path;

    // Getters and Setters
    public List<CameraConfig> getCamerasConfigurations() { return CamerasConfigurations; }
    public void setCamerasConfigurations(List<CameraConfig> configurations) { CamerasConfigurations = configurations; }

    public String getCamera_datas_path() { return camera_datas_path; }
    public void setCamera_datas_path(String camera_datas_path) { this.camera_datas_path = camera_datas_path; }
}