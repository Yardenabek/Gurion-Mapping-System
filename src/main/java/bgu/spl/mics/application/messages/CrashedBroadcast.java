package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class CrashedBroadcast implements Broadcast {
	
	private final String senderName; 
	private final String reason;
	
	public CrashedBroadcast(String senderName, String reason) {
		this.senderName = senderName;
		this.reason = reason;
	}
	
	public String getSenderName(){
		return senderName;
	}
	
	public String getReason() {
		return reason;
	}
}
