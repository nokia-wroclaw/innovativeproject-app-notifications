package dao;

import Model.Account;
import java.util.List;

public interface AccountDAO
{
	public List<Account> getAllAccounts();
    public Account getAccount(Integer accountID);
    public List<Account> AccountUserList(Integer userID);
    public void updateAccessToken(Integer id, String token);
    public void updateAccessTokenSecret(Integer id, String token);
    public void removeAccount(Integer accountID);
    public void createAccount(Account account);
    public void updateAgregation(Integer id, Integer aggregation);
    public void updateAgregationDate(Integer id, Integer date);
}