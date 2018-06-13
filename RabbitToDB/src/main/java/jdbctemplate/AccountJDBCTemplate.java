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
	public List<Account> AccountUserSourceList(Integer userID, Integer sourceID) {
		String SQL = "select * from nokiaapp.account where userID = ? and sourceID = ?";
        List<Account> accounts = jdbcTemplateObject.query(SQL, new Object[]{userID, sourceID}, new AccountMapper());
        return accounts;
	}

	@Override
	public List<Account> getAccountUserSource(Integer userID, Integer sourceID, String website) {
		String SQL = "select * from nokiaapp.account where userID = ? and sourceID = ? and accesstoken = ?";
		List<Account> account = jdbcTemplateObject.query(SQL, new Object[]{userID, sourceID, website}, new AccountMapper());
		return account;
	}
}
