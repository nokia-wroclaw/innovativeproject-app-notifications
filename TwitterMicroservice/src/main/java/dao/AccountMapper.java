package dao;

import org.springframework.jdbc.core.RowMapper;
import dao.Account;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountMapper implements RowMapper<Account>
{
    @Override
    public Account mapRow(ResultSet resultSet, int i) throws SQLException
    {
        Account account = new Account();
        account.setAccountID(resultSet.getInt("accountid"));
        account.setUserID(resultSet.getInt("userid"));
        account.setLogin(resultSet.getString("login"));
        account.setPassword(resultSet.getString("password"));
        account.setAccessToken(resultSet.getString("accesstoken"));
        account.setAccessTokenSecret(resultSet.getString("accesstokensecret"));
        return account;
    }
}
