package bgu.spl.tests;

import bgu.spl.mics.application.messages.TickBroadcast;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.services.CameraService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CameraTest {

    private Camera camera;
    private CameraService cameraService;

    @BeforeEach
    public void setUp() {
        // Create a list of detected objects for testing
        List<StampedDetectedObjects> detectedObjectsList = new ArrayList<>();
        detectedObjectsList.add(new StampedDetectedObjects(10, Arrays.asList(
                new DetectedObject("1", "Object 1"),
                new DetectedObject("ERROR", "Critical Error")
        )));
        detectedObjectsList.add(new StampedDetectedObjects(15, Arrays.asList(
                new DetectedObject("2", "Object 2"),
                new DetectedObject("3", "Object 3")
        )));

        // Initialize a real Camera object with test data
        camera = new Camera(1, 5, detectedObjectsList);

        // Initialize the CameraService with the Camera object
        cameraService = new CameraService(camera);
    }

    @Test
    public void testProcessValidTick() {
        // Test processing a valid TickBroadcast
        TickBroadcast tick = new TickBroadcast(10);

        cameraService.processTick(tick);

        // Verify the detected objects
        StampedDetectedObjects detected = camera.getDetectedObjectsForTick(10);
        assertEquals(2, detected.getDetectedObjects().size());
        assertEquals("1", detected.getDetectedObjects().get(0).getId());
    }

    @Test
    public void testProcessErrorTick() {
        // Test processing a TickBroadcast with an ERROR object
        TickBroadcast tick = new TickBroadcast(10);

        cameraService.processTick(tick);

        // Verify that the camera is deactivated due to an error
        assertEquals(false, camera.isUp());
    }

    @Test
    public void testProcessInvalidTick() {
        // Test processing a TickBroadcast that doesn't match the camera frequency
        TickBroadcast tick = new TickBroadcast(3);

        cameraService.processTick(tick);

        // Verify that no objects are detected
        StampedDetectedObjects detected = camera.getDetectedObjectsForTick(3);
        assertEquals(null, detected);
    }
}
