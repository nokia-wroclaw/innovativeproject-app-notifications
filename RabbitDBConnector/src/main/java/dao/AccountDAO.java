package dao;

import Model.Account;
import java.util.List;

public interface AccountDAO
{
    public Account getAccount(Integer accountID);
    public List<Account> AccountUserList(Integer userID);
    public List<Account> AccountUserSourceList(Integer userID, Integer sourceID);
    public List<Account> getAccountUserSource(Integer userID, Integer sourceID, String website);
    public void updateAccessToken(Integer id, String token);
    public void updateAccessTokenSecret(Integer id, String token);
    public void removeAccount(Integer accountID);
    public void createAccount(Account account);
}
