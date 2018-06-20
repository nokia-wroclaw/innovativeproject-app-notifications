package dao;

import Model.Subscription;
import java.util.List;

public interface SubscriptionDAO
{
    public List<Subscription> getSubscription(String name);
    public List<Subscription> getSubscription(Integer userID);
    public List<Subscription> listSubscriptions();
    public void removeSubscription(Integer userID, String name);
    public void createSubscription(Subscription subscription);
}
