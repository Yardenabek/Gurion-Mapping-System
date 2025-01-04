package bgu.spl.mics.application.configuration;

import java.util.List;

public class LiDarWorkersConfig {
    private List<LiDarConfig> LidarConfigurations;
    private String lidars_data_path;

    // Getters and Setters
    public List<LiDarConfig> getLidarConfigurations() { return LidarConfigurations; }
    public void setLidarConfigurations(List<LiDarConfig> configurations) { LidarConfigurations = configurations; }

    public String getLidars_data_path() { return lidars_data_path; }
    public void setLidars_data_path(String lidars_data_path) { this.lidars_data_path = lidars_data_path; }
}