package template;

import dao.UserDAO;
import Model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

public class UserTemplate implements UserDAO
{
    EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory)
    {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    public User getUser(Integer id)
    {
        Query query = entityManager.createQuery("select u from User u where u.userId = ?1",User.class);
        query.setParameter(1,id);
        return (User) query.getSingleResult();
    }
    
	public User getUserByToken(String userToken) {
		Query query = entityManager.createQuery("select u from User u where u.token = ?1",User.class);
        query.setParameter(1,userToken);
        return (User) query.getSingleResult();
	}
    
    public User getUserByLogin(String login)
    {
        Query query = entityManager.createQuery("select u from User u where u.login = ?1",User.class);
        query.setParameter(1,login);
        return (User) query.getSingleResult();
    }

    public List<User> listUsers()
    {
        Query query = entityManager.createQuery("select u from User u order by u.userId asc ",User.class);
        return  query.getResultList();
    }

    public void createUser(User user)
    {
        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();
    }

    public void changePassword(Integer id, String newPassword)
    {
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("update User u set u.password = ?1 where u.userId = ?2");
        query.setParameter(1,newPassword);
        query.setParameter(2,id);
        query.executeUpdate();
        entityManager.getTransaction().commit();
    }

    public void createToken(Integer id, String token)
    {
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("update User u set u.token = ?1 where u.userId = ?2");
        query.setParameter(1,token);
        query.setParameter(2,id);
        query.executeUpdate();
        entityManager.getTransaction().commit();
    }

    public void removeUser(Integer id)
    {
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("delete  from User u where u.userId = ?1");
        query.setParameter(1,id);
        query.executeUpdate();
        entityManager.getTransaction().commit();
    }
}
