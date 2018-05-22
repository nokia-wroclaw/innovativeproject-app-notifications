package dao;

import java.math.BigInteger;
import java.util.List;
import Model.Notification;

public interface NotificationDAO
{
    public Notification getNotification(Integer notificationID);
    public List<Notification> getUserNotification(Integer userID);
    public List<Notification> getNoReadNotification(Integer userID);
    public List<Notification> getCountNoReadUserNotification(Integer userID, Integer count, Integer offset);
    public List<Notification> getCountUserNotification(Integer userID, Integer count, Integer offset);
    public void updateNotification(Notification newNotification, BigInteger notificationID);
    public BigInteger findNotificationByTopic(String Topic, int user, int source);
    public BigInteger findNotificationByMessage(String Message, int user, int source);
    public void addNotification(Notification notification);
    public void IncrementCount(int oldCount, BigInteger notificationID);
    public void setRead(Integer notificationID);
    public void deleteNotification(Integer notificationID);
}
