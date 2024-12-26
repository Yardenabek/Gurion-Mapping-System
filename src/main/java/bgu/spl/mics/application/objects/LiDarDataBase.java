package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
	private final List<StampedCloudPoints> cloudPoints;
	private static LiDarDataBase instance = null;
	private final String filePath;
	
	 private LiDarDataBase(String filePath) {
		 this.cloudPoints = new ArrayList<>();
         this.filePath = filePath;
	    }
    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static synchronized LiDarDataBase getInstance(String filePath) {
        if (instance == null) {
        	instance = new LiDarDataBase(filePath);
        }
        return instance;
    }
    
    public synchronized void addStampedCloudPoints(StampedCloudPoints stampedCloudPoints) {
        cloudPoints.add(stampedCloudPoints);
    }
    
    public synchronized List<StampedCloudPoints> getStampedCloudPointsById(String id) {
        List<StampedCloudPoints> result = new ArrayList<>();
        for (StampedCloudPoints scp : cloudPoints) {
            if (scp.getId().equals(id)) {
                result.add(scp);
            }
        }
        return result;
    }
    
    public synchronized StampedCloudPoints getStampedCloudPointsByIdAndTime(String id, int time) {
        for (StampedCloudPoints scp : cloudPoints) {
            if (scp.getId().equals(id) && scp.getTime() == time) {
                return scp;
            }
        }
        return null;
    }

    public synchronized List<StampedCloudPoints> getAllStampedCloudPoints() {
        return new ArrayList<>(cloudPoints); // Return a copy to avoid external modification
    }

    public String getInitializationData() {
        return filePath;
    }
}
