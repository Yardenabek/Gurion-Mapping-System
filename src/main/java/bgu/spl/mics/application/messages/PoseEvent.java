<<<<<<< HEAD
package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Pose;

public class PoseEvent implements Event<Pose> {
	
	private final Pose pose;
	
	public PoseEvent(Pose pose) {
		this.pose = pose;
	}
	
	public Pose getPose() {
		return pose;
	}
=======
package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Pose;

public class PoseEvent implements Event<Boolean>{
	private final Pose pose;
	
	public PoseEvent(Pose pose) {
		this.pose = pose;
	}
	public Pose getPose() {
		return pose;
	}
>>>>>>> branch 'main' of https://github.com/Yardenabek/Gurion-Mapping-System.git
}