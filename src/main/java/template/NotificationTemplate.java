package template;

import dao.NotificationDAO;
import Model.Notification;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

public class NotificationTemplate implements NotificationDAO
{
    EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory)
    {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    public Notification getNotification(Integer notificationID)
    {
        Query query = entityManager.createQuery("select n from Notification n where n.notificationID = ?1", Notification.class);
        query.setParameter(1,notificationID);
        return (Notification) query.getSingleResult();
    }

    public List<Notification> getUserNotification(Integer userID)
    {
        Query query = entityManager.createQuery("select n from Notification n where n.userID = ?1", Notification.class);
        query.setParameter(1,userID);
        return  query.getResultList();
    }

    public List<Notification> getNoReadNotification(Integer userID)
    {
        Query query = entityManager.createQuery("select n from Notification n where n.userID = ?1 AND n.flag = false", Notification.class);
        query.setParameter(1,userID);
        return  query.getResultList();
    }

    public List<Notification> getCountNoReadUserNotification(Integer userID, Integer count, Integer offset)
    {
        Query query = entityManager.createQuery("select n from Notification n where n.userID = ?1 AND n.flag = false ", Notification.class);
        query.setParameter(1,userID);
        query.setFirstResult(offset);
        query.setMaxResults(count);
        return  query.getResultList();
    }
    //Dokończyć to koniecznie jutro
    public List<Notification> getCountUserNotification(Integer userID, Integer count, Integer offset)
    {
        Query query = entityManager.createQuery("select n from Notification n where n.userID = ?1 AND n.flag = false order by n.notificationID desc ", Notification.class);
        query.setParameter(1,userID);
        query.setFirstResult(offset);
        query.setMaxResults(count);
        return  query.getResultList();
    }

    public void setRead(Integer notificationID)
    {
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("update Notification n set n.flag = true where n.notificationID = ?1");
        query.setParameter(1,notificationID);
        query.executeUpdate();
        entityManager.getTransaction().commit();
    }

    public void deleteNotification(Integer notificationID)
    {
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("delete  from Notification n where n.notificationID = ?1");
        query.setParameter(1,notificationID);
        query.executeUpdate();
        entityManager.getTransaction().commit();
    }

	@Override
	public List<Notification> getCountUserNotificationFromSource(Integer userID, Integer sourceID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Notification> getCountUserNotificationFromSource(Integer userID, Integer count, Integer offset,
			Integer sourceID) {
		// TODO Auto-generated method stub
		return null;
	}
}
