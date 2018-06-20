package collection;

public class SourceNotificationCollection {

	private int sourceID;
	private int count;
	//private List<Notification> notifications;
	
	public void setSourceID(int source) {
		this.sourceID = source;
	}
	
	public int getSourceID() {
		return this.sourceID;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public int getCount() {
		return this.count;
	}
}
