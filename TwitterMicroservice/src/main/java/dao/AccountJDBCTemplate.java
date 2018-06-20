package dao;

import org.springframework.jdbc.core.JdbcTemplate;
import dao.Account;

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
    public List<Account> getAccountUserList(Integer userID)
    {
        String SQL = "select * from nokiaapp.account where userID = ?";
        List<Account> accounts = jdbcTemplateObject.query(SQL, new Object[]{userID}, new AccountMapper());
        return accounts;
    }

    @Override
    public List<Account> getAccountBySourceId(Integer sourceID)
    {
        String SQL = "select * from nokiaapp.account where sourceid = ?";
        List<Account> accounts = jdbcTemplateObject.query(SQL, new Object[]{sourceID}, new AccountMapper());
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
        String SQL = "INSERT INTO nokiaapp.account (userid, login, password) VALUES (?,?,?)";
        jdbcTemplateObject.update(SQL, account.getUserID(),account.getLogin(), account.getPassword());
    }
}
