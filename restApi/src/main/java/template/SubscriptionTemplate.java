package template;

import dao.SubscriptionDAO;
import Model.Subscription;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

//Nie robie jej na razie
public class SubscriptionTemplate implements SubscriptionDAO
{
    EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory)
    {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    public List<Subscription> getSubscription(String name)
    {
        Query query = entityManager.createQuery("select s from Subscription s where s.name = ?1", Subscription.class);
        query.setParameter(1,name);
        return  query.getResultList();
    }

    public List<Subscription> getSubscription(Integer userID)
    {
        return null;
    }

    public List<Subscription> listSubscriptions()
    {
        return null;
    }

    public void removeSubscription(Integer userID, String name)
    {

    }

    public void createSubscription(Subscription subscription)
    {

    }
}
