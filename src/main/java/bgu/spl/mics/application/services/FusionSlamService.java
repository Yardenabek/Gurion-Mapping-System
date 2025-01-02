package bgu.spl.mics.application.services;

import java.util.List;

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
                 LandMark landmark = fusionSlam.getLandmark(object.getId());
                 Pose currentPose = fusionSlam.getPoseAtTime(object.getTime());
                
                 List<CloudPoint> transformedPoints = fusionSlam.transformToGlobalFrame(object.getCoordinates(), currentPose);
                 
                 if (landmark == null) {
                     
                     fusionSlam.addLandMark(new LandMark(object.getId(), object.getDescription(), transformedPoints));
                 } else {
                     
                     fusionSlam.updateLandmark(object.getId(), transformedPoints);
                 }
             }
         });
    
        subscribeEvent(PoseEvent.class,poseEvent -> {
        	fusionSlam.addPose(poseEvent.getPose());
        });
        subscribeBroadcast(TickBroadcast.class,tick->{
        	fusionSlam.updateTick(tick.getCurrentTick());
        });
        subscribeBroadcast(TerminatedBroadcast.class,terminated->{
        	terminate();
        });
        subscribeBroadcast(CrashedBroadcast.class,crashed->{
        	terminate();
        });


    }
}
