package bgu.spl.mics.application.configuration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

public class ConfigurationParser {
    public static Configuration parseConfiguration(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, Configuration.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Camera parseCameraData(String filePath, String cameraKey, int cameraId, int frequency) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            // Parse the JSON file as a JsonObject to access keyed data
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            
            // Extract the list corresponding to the given camera key
            JsonElement cameraDataElement = jsonObject.get(cameraKey);
            if (cameraDataElement == null) {
                throw new IllegalArgumentException("Camera key not found in the file: " + cameraKey);
            }

            // Deserialize the data into a list of StampedDetectedObjects
            Type type = new TypeToken<List<StampedDetectedObjects>>() {}.getType();
            List<StampedDetectedObjects> detectedObjectsList = gson.fromJson(cameraDataElement, type);

            // Create and return the Camera object
            return new Camera(cameraId, frequency, detectedObjectsList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static List<Pose> parsePoseData(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<List<Pose>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
