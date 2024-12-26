package bgu.spl.mics.application.messages;

import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;

public class DetectObjectsEvent implements Event<Boolean>{
	private final int time;
	private final List<DetectedObject> detectedObjects;
	
	public DetectObjectsEvent(int time,List<DetectedObject> detectedObjects) {
		this.time = time;
		this.detectedObjects = detectedObjects;
	}
}