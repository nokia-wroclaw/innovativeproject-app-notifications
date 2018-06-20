package collection;

import java.util.ArrayList;
import java.util.List;

import Model.Notification;

public class NotificationCollection {

	private List<Notification> notifications;
	
	
	public NotificationCollection() {
		this.notifications = new ArrayList<Notification>();
	}
	
	public void createNotificationCollection(List<Notification> list) {
		this.notifications.addAll(list);
	}
	
	public List<Notification> getNotifications() {
		return notifications;
	}
}
