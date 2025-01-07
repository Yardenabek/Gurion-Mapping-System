package bgu.spl.tests;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.FusionSlamService;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FusionSlamTest {

    private FusionSlam fusionSlam;
    private FusionSlamService fusionSlamService;

    @Before
    public void setUp() {
        // Initialize FusionSlam and FusionSlamService before each test
        fusionSlam = FusionSlam.getInstance();
        fusionSlamService = new FusionSlamService(fusionSlam);
        StatisticalFolder.getInstance().reset(); // Reset statistics if necessary
    }

    @After
    public void tearDown() {
        // Clean up after each test
        fusionSlam.getLandMarks().clear();
        fusionSlam.getPoses().clear();
        fusionSlam.updateTick(0);
        StatisticalFolder.getInstance().reset();
    }

    @Test
    public void testAddNewLandmark() {
        // Arrange
        TrackedObject trackedObject = new TrackedObject("1", 1, "Object 1", List.of(new CloudPoint(1, 1)));
        Pose pose = new Pose(0, 0, 0, 1);
        fusionSlam.addPose(pose);
        TrackedObjectsEvent event = new TrackedObjectsEvent(List.of(trackedObject));

        // Act
        fusionSlamService.processTrackedObjects(event);
        
        // Assert
        LandMark landmark = fusionSlam.getLandmark("1");
        assertNotNull("Landmark should be created for new tracked object.", landmark);
        assertEquals("Object 1", landmark.getDescription());
        assertEquals(1, StatisticalFolder.getInstance().getLandmarks());
    }

    @Test
    public void testUpdateExistingLandmark() {
        // Arrange
        LandMark existingLandmark = new LandMark("1", "Existing Object", List.of(new CloudPoint(1, 1)));
        fusionSlam.addLandMark(existingLandmark);
        TrackedObject trackedObject = new TrackedObject("1", 2, "Existing Object", List.of(new CloudPoint(2, 2)));
        Pose pose = new Pose(0, 0, 0, 2);
        fusionSlam.addPose(pose);
        TrackedObjectsEvent event = new TrackedObjectsEvent(List.of(trackedObject));

        // Act
        fusionSlamService.processTrackedObjects(event);

        // Assert
        LandMark updatedLandmark = fusionSlam.getLandmark("1");
        assertNotNull("Landmark should still exist after update.", updatedLandmark);
        assertEquals("Existing Object", updatedLandmark.getDescription());
        assertEquals(1, StatisticalFolder.getInstance().getLandmarks());
        assertEquals(2.0, updatedLandmark.getCoordinates().get(0).getX(), 0.01);
        assertEquals(2.0, updatedLandmark.getCoordinates().get(0).getY(), 0.01);
    }

    @Test
    public void testGlobalCoordinatesTransformation() {
        // Arrange
        Pose pose = new Pose(5, 5, 90, 1); // Robot is at (5,5) with 90-degree rotation
        fusionSlam.addPose(pose);
        TrackedObject trackedObject = new TrackedObject("1", 1, "Object 1", List.of(new CloudPoint(1, 1)));
        TrackedObjectsEvent event = new TrackedObjectsEvent(List.of(trackedObject));

        // Act
        fusionSlamService.processTrackedObjects(event);

        // Assert
        LandMark landmark = fusionSlam.getLandmark("1");
        assertNotNull("Landmark should be created.", landmark);
        // Verify transformed coordinates
        assertEquals(4.0, landmark.getCoordinates().get(0).getX(), 0.01);
        assertEquals(6.0, landmark.getCoordinates().get(0).getY(), 0.01);
    }

    @Test
    public void testEmptyTrackedObjectsList() {
        // Arrange
        TrackedObjectsEvent event = new TrackedObjectsEvent(List.of());

        // Act
        fusionSlamService.processTrackedObjects(event);

        // Assert
        assertTrue("No landmarks should be added for an empty tracked objects list.", fusionSlam.getLandMarks().isEmpty());
        assertEquals(0, StatisticalFolder.getInstance().getLandmarks());
    }

    @Test
    public void testTrackedObjectsStatistics() {
        // Arrange
        TrackedObject trackedObject1 = new TrackedObject("1", 1, "Object 1", List.of(new CloudPoint(1, 1)));
        TrackedObject trackedObject2 = new TrackedObject("2", 2, "Object 2", List.of(new CloudPoint(2, 2)));
        Pose pose = new Pose(0, 0, 0, 1);
        fusionSlam.addPose(pose);
        TrackedObjectsEvent event = new TrackedObjectsEvent(List.of(trackedObject1, trackedObject2));

        // Act
        fusionSlamService.processTrackedObjects(event);

        // Assert
        assertEquals(2, StatisticalFolder.getInstance().getTrackedObjects());
    }
}
