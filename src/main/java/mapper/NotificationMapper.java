package mapper;

import org.springframework.jdbc.core.RowMapper;
import Model.Notification;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationMapper implements RowMapper<Notification>
{
    @Override
    public Notification mapRow(ResultSet resultSet, int i) throws SQLException
    {
        Notification notification = new Notification();
        notification.setNotificationID(BigInteger.valueOf(resultSet.getInt("notificationid")));
        notification.setUserID(resultSet.getInt("userid"));
        notification.setSourceID(resultSet.getInt("sourceid"));
        notification.setFlag(resultSet.getBoolean("flag"));
        notification.setTopic(resultSet.getString("topic"));
        notification.setMessage(resultSet.getString("message"));
        notification.setTimestamp(resultSet.getString("time"));
        notification.setPriority(resultSet.getInt("priority"));
        return notification;
    }
}
