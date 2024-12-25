package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
	private LandMark[] Landmarks;
    private List<Pose> Poses;
    
    private FusionSlam() {
    	this.Landmarks = new LandMark[];
    	this.Poses = new ArrayList();
    }
    // Singleton instance holder
    private static class FusionSlamHolder {
        private static final FusionSlam Instance = new FusionSlam();
    }
}
