package jdbctemplate;

import org.springframework.jdbc.core.JdbcTemplate;

import dao.SubscriptionDAO;
import mapper.SubscriptionMapper;
import Model.Subscription;

import javax.sql.DataSource;
import java.util.List;

public class SubscriptionJDBCTemplate implements  SubscriptionDAO
{
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Subscription> getSubscription(String name)
    {
        String SQL = "select * from nokiaapp.subscription where name = ?";
        List<Subscription> subscription = jdbcTemplateObject.query(SQL, new Object[]{name}, new SubscriptionMapper());
        return subscription;
    }

    @Override
    public List<Subscription> getSubscription(Integer userID)
    {
        String SQL = "select * from nokiaapp.subscription where userID = ?";
        List<Subscription> subscription = jdbcTemplateObject.query(SQL, new Object[]{userID}, new SubscriptionMapper());
        return subscription;
    }

    @Override
    public void createSubscription(Subscription subscription)
    {
        String SQL = "INSERT INTO nokiaapp.subscription (userid, sourceid, name) VALUES (?,?,?)";
        jdbcTemplateObject.update(SQL, subscription.getUserID(), subscription.getSourceID(), subscription.getName());
    }

    @Override
    public List<Subscription> listSubscriptions()
    {
        String SQL = "select * from nokiaapp.subscription";
        List<Subscription> subscription = jdbcTemplateObject.query(SQL, new SubscriptionMapper());
        return subscription;
    }

    @Override
    public void removeSubscription(Integer userID, String name)
    {
        String SQL = "delete from nokiaapp.subscription where userid = ? and name =?";
        jdbcTemplateObject.update(SQL, userID, name);
    }
}