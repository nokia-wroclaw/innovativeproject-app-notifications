package template;

import dao.AccountDAO;
import Model.Account;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

public class AccountTemplate implements AccountDAO
{
    EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory)
    {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    public Account getAccount(Integer accountID)
    {
        Query query = entityManager.createQuery("select a from Account a where a.accountID = ?1", Account.class);
        query.setParameter(1,accountID);
        return (Account) query.getSingleResult();
    }

    public List<Account> AccountUserList(Integer userID)
    {
        Query query = entityManager.createQuery("select a from Account a where a.userID = ?1", Account.class);
        query.setParameter(1,userID);
        return query.getResultList();
    }

    public void updateAccessToken(Integer id, String token)
    {
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("update Account a set a.accessToken = ?1 where a.accountID = ?2");
        query.setParameter(1,id);
        query.setParameter(2,token);
        query.executeUpdate();
        entityManager.getTransaction().commit();
    }

    public void updateAccessTokenSecret(Integer id, String token)
    {
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("update Account a set a.accessTokenSecret = ?1 where a.accountID = ?2");
        query.setParameter(1,id);
        query.setParameter(2,token);
        query.executeUpdate();
        entityManager.getTransaction().commit();
    }

    public void removeAccount(Integer accountID)
    {
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("delete  from Account a where a.accountID = ?1");
        query.setParameter(1,accountID);
        query.executeUpdate();
        entityManager.getTransaction().commit();
    }

    public void createAccount(Account account)
    {
        entityManager.getTransaction().begin();
        entityManager.persist(account);
        entityManager.getTransaction().commit();
    }

	@Override
	public List<Account> getAllAccounts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateAggregation(Integer id, Integer aggregation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAggregationDate(Integer id, Integer date) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAggregationBy(Integer id, Integer by) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAggregationKeys(Integer id, String keys) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUserAccount(Integer userID) {
		// TODO Auto-generated method stub
		
	}
}
