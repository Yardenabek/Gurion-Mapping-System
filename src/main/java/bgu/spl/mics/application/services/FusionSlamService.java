package bgu.spl.mics.application.services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
	private FusionSlam fusionSlam;
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeEvent(TrackedObjectsEvent.class,trackedEvent -> {
        	 List<TrackedObject> trackedObjects = trackedEvent.getTrackedObjects();
             
             for (TrackedObject object : trackedObjects) {
            	 StatisticalFolder.getInstance().incrementTrackedObjects();
                 LandMark landmark = fusionSlam.getLandmark(object.getId());
                 Pose currentPose = fusionSlam.getPoseAtTime(object.getTime());
                
                 List<CloudPoint> transformedPoints = fusionSlam.transformToGlobalFrame(object.getCoordinates(), currentPose);
                 
                 if (landmark == null) {
                     StatisticalFolder.getInstance().incrementLandmarks();
                     fusionSlam.addLandMark(new LandMark(object.getId(), object.getDescription(), transformedPoints));
                 } 
                 else {
                     
                     fusionSlam.updateLandmark(object.getId(), transformedPoints);
                 }
             }
         });
    
        subscribeEvent(PoseEvent.class,poseEvent -> {
        	fusionSlam.addPose(poseEvent.getPose());
        });
        subscribeBroadcast(TickBroadcast.class,tick->{
        	StatisticalFolder.getInstance().incrementSystemRuntime();
        	fusionSlam.updateTick(tick.getCurrentTick());
        });
        subscribeBroadcast(TerminatedBroadcast.class,terminated->{
        	writeFinalResults(terminated.getSenderName());
        	terminate();
        });
        subscribeBroadcast(CrashedBroadcast.class,crashed->{
        	writeErrorResults(crashed.getSenderName(),crashed.getReason());
        	terminate();
        });


    }
    
    private void writeFinalResults(String terminatedName) {
        Map<String, Object> outputData = new HashMap<>();
        outputData.put("Terminated by:", terminatedName);
        // Statistics Section
        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("systemRuntime", StatisticalFolder.getInstance().getsystemRuntime());
        statistics.put("numDetectedObjects", StatisticalFolder.getInstance().getDetectedObjects());
        statistics.put("numTrackedObjects", StatisticalFolder.getInstance().getTrackedObjects());
        statistics.put("numLandmarks", StatisticalFolder.getInstance().getLandmarks());
        outputData.put("statistics", statistics);

        // World Map Section
        List<LandMark> landmarks = fusionSlam.getLandMarks();
        outputData.put("worldMap", landmarks);

        // Convert to JSON and write to file
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("output_file.json")) {
            gson.toJson(outputData, writer);
            System.out.println("Output file written successfully.");
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }
    private void writeErrorResults(String sendername, String reason) {
        Map<String, Object> outputData = new HashMap<>();
        //Error description and faulty sensor.
        outputData.put("Error:", reason);
        outputData.put("Faulty sensor:", sendername);
        //TODO:include last frames from each camera and lidar worker.
        
        //poses section
        List<Pose> poses = FusionSlam.getInstance().getPoses();
        outputData.put("Poses", poses);
        
        // Statistics Section
        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("systemRuntime", StatisticalFolder.getInstance().getsystemRuntime());
        statistics.put("numDetectedObjects", StatisticalFolder.getInstance().getDetectedObjects());
        statistics.put("numTrackedObjects", StatisticalFolder.getInstance().getTrackedObjects());
        statistics.put("numLandmarks", StatisticalFolder.getInstance().getLandmarks());
        outputData.put("statistics", statistics);
        
        // Convert to JSON and write to file
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("output_file.json")) {
            gson.toJson(outputData, writer);
            System.out.println("Output file written successfully.");
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }
    public void processTrackedObjects(TrackedObjectsEvent event) {
        List<TrackedObject> trackedObjects = event.getTrackedObjects();
        for (TrackedObject object : trackedObjects) {
            StatisticalFolder.getInstance().incrementTrackedObjects();
            LandMark landmark = fusionSlam.getLandmark(object.getId());
            Pose currentPose = fusionSlam.getPoseAtTime(object.getTime());
            List<CloudPoint> transformedPoints = fusionSlam.transformToGlobalFrame(object.getCoordinates(), currentPose);

            if (landmark == null) {
                StatisticalFolder.getInstance().incrementLandmarks();
                fusionSlam.addLandMark(new LandMark(object.getId(), object.getDescription(), transformedPoints));
            } else {
                fusionSlam.updateLandmark(object.getId(), transformedPoints);
            }
        }
    }

    

}
