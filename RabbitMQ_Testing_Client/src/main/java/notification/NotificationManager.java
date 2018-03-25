package notification;

import application.ApplicationManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class NotificationManager {
    private ArrayList<Notification> notifications;
    private NotificationReceiver receiver;

    private ApplicationManager manager;

    public NotificationManager(ApplicationManager manager){

        receiver = new NotificationReceiver(this);
        receiver.start();

        this.manager = manager;
        notifications = new ArrayList<Notification>();
    }

    public ArrayList<Notification> getNotifications(){
        return notifications;
    }

    public void removeNotification(Notification notification){
        notifications.remove(notification);
        manager.refreshList();
    }

    public void addNotification(Notification notification){
        notifications.add(notification);

        manager.refreshList();
    }

    public void closeReceivers(){
        try {
            receiver.closeConnection();
            receiver.join();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
