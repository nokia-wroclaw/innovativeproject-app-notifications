package Model;

import java.sql.Timestamp;

public class Notification {

	private int notificationID;
	private int userID;
	private int sourceID;
	private boolean flag;
	private String topic;
	private String message;
	private Timestamp time;
	private int priority;
	
	public Notification(int nID, int uID, int sID, boolean flag, String topic, String message, Timestamp time, int prio) {
		this.notificationID = nID;
		this.userID = uID;
		this.sourceID = sID;
		this.flag = flag;
		this.topic = topic;
		this.message = message;
		this.time = time;
		this.priority = prio;
	}
	
	public int getNotificationID() {
		return notificationID;
	}
	
	public void setNotificationID(int nID) {
		this.notificationID = nID;
	}
	
	public int getUserID() {
		return userID;
	}
	
	public void setUserID(int uID) {
		this.userID = uID;
	}
	
	public int getSourceID() {
		return sourceID;
	}
	
	public void setSourceID(int sID) {
		this.sourceID = sID;
	}
	
	public boolean getFlag() {
		return flag;
	}
	
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String m) {
		this.message = m;
	}
	
	public Timestamp getTime() {
		return time;
	}
	
	public void setTime(Timestamp t) {
		this.time = t;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int p) {
		this.priority = p;
	}
}
