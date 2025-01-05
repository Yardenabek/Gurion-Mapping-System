package bgu.spl.mics.application;


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
        List<Thread> ServiceThreads = new ArrayList<>();
        
        for(CameraConfig cameraconfig : configuration.getCameras().getCamerasConfigurations()) {
        	Camera cam = ConfigurationParser.parseCameraData(
        			configuration.getCameras().getCamera_datas_path(),
        			cameraconfig.getCamera_key(), cameraconfig.getId(),
        			cameraconfig.getFrequency());
        	CameraService cameraservice = new CameraService(cam);
        	Thread CameraThread = new Thread(cameraservice);
        	ServiceThreads.add(CameraThread);
        }
        
        for(LiDarConfig lidar : configuration.getLiDarWorkers().getLidarConfigurations()) {
        	LiDarWorkerTracker lidarworker = new LiDarWorkerTracker(
        			lidar.getId(),
        			lidar.getFrequency(),
        			configuration.getLiDarWorkers().getLidars_data_path());
        	LiDarService lidarservice = new LiDarService(lidarworker);
        	Thread lidarthread = new Thread(lidarservice);
        	ServiceThreads.add(lidarthread);
        }
        
        GPSIMU gpsimu = new GPSIMU(
        		0,
        		ConfigurationParser.parsePoseData(configuration.getPoseJsonFile()));
        PoseService poseService = new PoseService(gpsimu);
        Thread posethread = new Thread(poseService);
        ServiceThreads.add(posethread);
        
        TimeService timeService = new TimeService(
        		configuration.getTickTime(),
        		configuration.getDuration());
        Thread timethread = new Thread(timeService);
        ServiceThreads.add(timethread);
        
        FusionSlam fusionSLAM = FusionSlam.getInstance();
        FusionSlamService fusionSlamService = new FusionSlamService(fusionSLAM);
        Thread fusionSlamThread = new Thread(fusionSlamService);
        ServiceThreads.add(fusionSlamThread);
        //Start the simulation.
        
        ServiceThreads.forEach(Thread::start);

        // Wait for all threads to finish
        for (Thread thread : ServiceThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("Simulation interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("Simulation complete."); 
    }
}
