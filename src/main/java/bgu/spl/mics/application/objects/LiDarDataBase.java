package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
	private List<StampedCloudPoints> cloudPoints;
	private static LiDarDataBase instance = null;
	private final String filePath;
	
	 private LiDarDataBase(String filePath) {
         this.filePath = filePath;
         this.cloudPoints = new ArrayList<>();
         Gson gson = new Gson();
         try (FileReader reader = new FileReader(filePath)) {
             Type listType = new TypeToken<List<StampedCloudPoints>>() {}.getType();
             cloudPoints = gson.fromJson(reader, listType);
         }
         catch (Exception e) {
             e.printStackTrace();
         }
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
    
    public synchronized StampedCloudPoints getStampedCloudPointsById(String id) {
        for (StampedCloudPoints scp : cloudPoints) {
            if (scp.getId().equals(id)) {
                return scp;
            }
        }
        return null;
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
