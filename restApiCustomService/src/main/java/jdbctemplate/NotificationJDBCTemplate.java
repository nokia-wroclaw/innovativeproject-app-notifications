package jdbctemplate;

import org.springframework.jdbc.core.JdbcTemplate;
import model.Notification;
import dao.NotificationDAO;
import mapper.NotificationMapper;

import javax.sql.DataSource;
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

    @Override
	public List<Notification> getCountUserNotificationFromSource(Integer userID, Integer sourceID) {
		String SQL = "select * from nokiaapp.noti_desc where userID = ? AND sourceID = ?";
        List<Notification> notifications = jdbcTemplateObject.query(SQL, new Object[]{userID, sourceID}, new NotificationMapper());
        return notifications;
	}
    
	@Override
	public List<Notification> getCountUserNotificationFromSource(Integer userID, Integer count, Integer offset,
			Integer sourceID) {
		String SQL = "select * from nokiaapp.noti_desc where userID = ? AND sourceID = ? LIMIT ? OFFSET ?";
        List<Notification> notifications = jdbcTemplateObject.query(SQL, new Object[]{userID, sourceID, count, offset}, new NotificationMapper());
        return notifications;
	}
}