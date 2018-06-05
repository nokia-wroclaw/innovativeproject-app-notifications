package dao;

import dao.Account;
import java.util.List;

public interface AccountDAO
{
    public Account getAccount(Integer accountID);
    public List<Account> getAccountUserList(Integer userID);
    public List<Account> getAccountBySourceId(Integer sourceID);
    public void updateAccessToken(Integer id, String token);
    public void updateAccessTokenSecret(Integer id, String token);
    public void removeAccount(Integer accountID);
    public void createAccount(Account account);
}
