package Model;

public class Subscription {
	private int sourceID;
	private int userID;
	private String name;
	
	public Subscription(int user, int source, String name) {
		this.sourceID = source;
		this.userID = user;
		this.name = name;
	}
	
	public int getSource() {
		return sourceID;
	}
	
	public void setSource(int s) {
		this.userID = s;
	}
	
	public int getUser() {
		return userID;
	}
	
	public void setUser(int u) {
		this.userID = u;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
