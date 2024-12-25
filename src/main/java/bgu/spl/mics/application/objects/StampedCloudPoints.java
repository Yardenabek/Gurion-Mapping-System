package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
   private final String id;
   private final int time;
   private final List<CloudPoint> CloudPoints;
   
   public StampedCloudPoints(String id,int time,List<CloudPoint> CloudPoints) {
	   this.id = id;
	   this.time = time;
	   this.CloudPoints = CloudPoints;
   }
   public String getId() {
	   return id;
   }
   public int getTime() {
	   return time;
   }
   public List<CloudPoint> getCloudPoints(){
	   return CloudPoints;
   }
}
