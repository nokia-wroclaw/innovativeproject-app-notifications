package jdbctemplate;

import org.springframework.jdbc.core.JdbcTemplate;
import Model.Notification;
import dao.NotificationDAO;
import mapper.NotificationMapper;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.util.List;

public class NotificationJDBCTemplate implements NotificationDAO
{
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    @Override
    public Notification getNotification(Integer notificationID)
    {
        String SQL = "select * from nokiaapp.notification where notificationid = ?";
        Notification notification = jdbcTemplateObject.queryForObject(SQL, new Object[]{notificationID}, new NotificationMapper());
        return notification;
    }

    @Override
    public List<Notification> getUserNotification(Integer userID)
    {
        String SQL = "select * from nokiaapp.notification where userID = ?";
        List<Notification> notifications = jdbcTemplateObject.query(SQL, new Object[]{userID}, new NotificationMapper());
        return notifications;
    }

    @Override
    public List<Notification> getNoReadNotification(Integer userID)
    {
        String SQL = "select * from nokiaapp.notification where userID = ? AND flag = FALSE ";
        List<Notification> notifications = jdbcTemplateObject.query(SQL, new Object[]{userID}, new NotificationMapper());
        return notifications;
    }

    @Override
    public List<Notification> getCountNoReadUserNotification(Integer userID, Integer count, Integer offset)
    {
        String SQL = "select * from nokiaapp.noti_desc where userID = ? AND flag = FALSE LIMIT ?  OFFSET ?";
        List<Notification> notifications = jdbcTemplateObject.query(SQL, new Object[]{userID, count, offset}, new NotificationMapper());
        return notifications;
    }

    @Override
    public List<Notification> getCountUserNotification(Integer userID, Integer count, Integer offset)
    {
        String SQL = "select * from nokiaapp.noti_desc where userID = ? LIMIT ? OFFSET ?";
        List<Notification> notifications = jdbcTemplateObject.query(SQL, new Object[]{userID, count, offset}, new NotificationMapper());
        return notifications;
    }


    @Override
    public BigInteger findNotificationByTopic(String Topic, int user, int source) {
        String SQL = "select * from nokiaapp.notification where topic = ? and userid=? and sourceid =? and flag=false";
        List<Notification> notifications = jdbcTemplateObject.query(SQL, new Object[]{Topic,user,source}, new NotificationMapper());
        if(notifications.size()==0){
            return BigInteger.valueOf(-1);
        }else {
            return notifications.get(0).getNotificationID();

        }
    }

    @Override
    public BigInteger findNotificationByMessage(String Message, int user, int source)
    {String SQL = "select * from nokiaapp.notification where message = ? and userid=? and sourceid =? and flag=false";
        List<Notification> notifications = jdbcTemplateObject.query(SQL, new Object[]{Message,user,source}, new NotificationMapper());
        if(notifications.size()==0){
            return BigInteger.valueOf(-1);
        }else {
            return notifications.get(0).getNotificationID();
        }
    }

    @Override
    public void IncrementCount(int oldCount, BigInteger notificationID){
        String SQL = "update nokiaapp.notification SET count=? where notificationid=?";
        jdbcTemplateObject.update(SQL,new Object[]{oldCount+1});
    }

    @Override
    public void updateNotification(Notification newNotification, BigInteger notificationID) {
        String SQL = "update nokiaapp.notification SET userid=?, sourceid=?, topic=?, message=?, priority=?, flag=? where notificationid=?";
        jdbcTemplateObject.update(SQL,
                new Object[]{newNotification.getUserID(), newNotification.getSourceID(), newNotification.getTopic(), newNotification.getMessage(), newNotification.getPriority(), false, notificationID});
    }

    @Override
    public void addNotification(Notification notification) {
        String SQL = "insert into nokiaapp.notification (userid, sourceid, topic, message, priority, flag) values (?, ?, ?, ?, ?, ?)";
        jdbcTemplateObject.update(SQL,
                new Object[]{notification.getUserID(), notification.getSourceID(), notification.getTopic(), notification.getMessage(), notification.getPriority(), false});
    }

    @Override
    public void setRead(Integer notificationID)
    {
        String SQL = "update nokiaapp.notification set flag = true where notificationid = ?";
        jdbcTemplateObject.update(SQL, notificationID);
    }
    
    @Override
    public void deleteNotification(Integer notificationID)
    {
        String SQL = "delete from nokiaapp.notification where notificationid = ?";
        jdbcTemplateObject.update(SQL, notificationID);
    }
}
