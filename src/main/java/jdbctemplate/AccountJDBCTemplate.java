package jdbctemplate;

import org.springframework.jdbc.core.JdbcTemplate;
import Model.Account;
import dao.AccountDAO;
import mapper.AccountMapper;

import javax.sql.DataSource;
import java.util.List;

public class AccountJDBCTemplate implements AccountDAO
{
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Account> getAllAccounts()
    {
        String SQL = "select * from nokiaapp.account";
        List<Account> account = jdbcTemplateObject.query(SQL, new AccountMapper());
        return account;
    }
    
    @Override
    public Account getAccount(Integer accountID)
    {
        String SQL = "select * from nokiaapp.account where accountID = ?";
        Account account = jdbcTemplateObject.queryForObject(SQL,new Object[]{accountID}, new AccountMapper());
        return account;
    }

    @Override
    public List<Account> AccountUserList(Integer userID)
    {
        String SQL = "select * from nokiaapp.account where userID = ?";
        List<Account> accounts = jdbcTemplateObject.query(SQL, new Object[]{userID}, new AccountMapper());
        return accounts;
    }

    @Override
    public void updateAccessToken(Integer id, String token)
    {
        String SQL = "update nokiaapp.user set accesstoken = ? where accountid = ?";
        jdbcTemplateObject.update(SQL, token, id);
    }

    @Override
    public void updateAccessTokenSecret(Integer id, String token)
    {
        String SQL = "update nokiaapp.user set accesstokensecret = ? where accountid = ?";
        jdbcTemplateObject.update(SQL, token, id);
    }

    @Override
    public void removeAccount(Integer accountID)
    {
        String SQL = "delete from nokiaapp.account where accountid = ?";
        jdbcTemplateObject.update(SQL, accountID);
    }

    @Override
    public void createAccount(Account account)
    {
        String SQL = "INSERT INTO nokiaapp.account (userid, login, password, accesstoken, accesstokensecret, sourceid) VALUES (?,?,?,?,?,?)";
        jdbcTemplateObject.update(SQL, account.getUserID(),account.getLogin(), account.getPassword(), account.getAccessToken(), account.getAccessTokenSecret(), account.getSourceID());
    }

	@Override
	public void updateAggregation(Integer id, Integer aggregation) {
		String SQL = "update nokiaapp.account set aggregation = ? where accountid = ?";
        jdbcTemplateObject.update(SQL, aggregation, id);
	}

	@Override
	public void updateAggregationDate(Integer id, Integer date) {
		String SQL = "update nokiaapp.account set aggregationdate = ? where accountid = ?";
		jdbcTemplateObject.update(SQL, date, id);
		
	}
	
	@Override
	public void updateAggregationBy(Integer id, Integer by) {
		String SQL = "update nokiaapp.account set aggregationtype = ? where accountid = ?";
		jdbcTemplateObject.update(SQL, by, id);
		
	}
	
	@Override
	public void updateAggregationKeys(Integer id, String keys) {
		String SQL = "update nokiaapp.account set aggregationkey = ? where accountid = ?";
		jdbcTemplateObject.update(SQL, keys, id);
		
	}
}
