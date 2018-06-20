package dao;

import model.Account;
import java.util.List;

public interface AccountDAO
{
	public List<Account> getAllAccounts();
    public Account getAccount(Integer accountID);
    public Account getAccountByToken(Integer sourceID, String accessToken);
    public List<Account> AccountUserList(Integer userID);
    public void updateAccessToken(Integer id, String token);
    public void updateAccessTokenSecret(Integer id, String token);
    public void removeAccount(Integer accountID);
    public void createAccount(Account account);
    public void updateAggregation(Integer id, Integer aggregation);
    public void updateAggregationDate(Integer id, Integer date);
    public void updateAggregationBy(Integer id, Integer by);
    public void updateAggregationKeys(Integer id, String keys);
}
