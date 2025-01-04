package bgu.spl.mics.application;

import com.google.gson.Gson;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.application.configuration.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        System.out.println("Hello World!");
        // Parse configuration file.
        Configuration configuration = ConfigurationParser.parseConfiguration(args[0]);
       
        // Initialize system components and services.
        List<CameraService> CameraServices = new ArrayList<>();
        List<LiDarService> LidarServices = new ArrayList<>();
        
        for(CameraConfig cameraconfig : configuration.getCameras().getCamerasConfigurations()) {
        	Camera cam = ConfigurationParser.parseCameraData(configuration.getCameras().getCamera_datas_path(), cameraconfig.getCamera_key(), cameraconfig.getId(), cameraconfig.getFrequency());
        	CameraServices.add(new CameraService(cam));
        }
        for(LiDarConfig lidar : configuration.getLiDarWorkers().getLidarConfigurations()) {
        	LidarServices.add(new LiDarService(new LiDarWorkerTracker(lidar.getId(),lidar.getFrequency(),configuration.getLiDarWorkers().getLidars_data_path())));
        }
        PoseService poseService = new PoseService(new GPSIMU(0,ConfigurationParser.parsePoseData(configuration.getPoseJsonFile())));
        TimeService timeService = new TimeService(configuration.getTickTime(),configuration.getDuration());
        // TODO: Start the simulation.
    }
}
