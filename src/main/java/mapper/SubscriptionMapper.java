package mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import Model.Subscription;

public class SubscriptionMapper implements RowMapper<Subscription>
{
    @Override
    public Subscription mapRow(ResultSet resultSet, int i) throws SQLException
    {
        Subscription subscription = new Subscription();
        subscription.setUserID(resultSet.getInt("userid"));
        subscription.setSourceID(resultSet.getInt("sourceid"));
        subscription.setName(resultSet.getString("name"));
        return subscription;
    }
}