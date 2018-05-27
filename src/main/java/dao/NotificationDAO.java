package dao;

import java.util.List;
import Model.Notification;

public interface NotificationDAO
{
    public Notification getNotification(Integer notificationID);
    public List<Notification> getUserNotification(Integer userID);
    public List<Notification> getNoReadNotification(Integer userID);
    public List<Notification> getCountNoReadUserNotification(Integer userID, Integer count, Integer offset);
    public List<Notification> getCountUserNotification(Integer userID, Integer count, Integer offset);
    public void setRead(Integer notificationID);
    public void deleteNotification(Integer notificationID);
}
