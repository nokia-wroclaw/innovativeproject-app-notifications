package model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Table(name ="nokiaapp.notification")
public class Notification implements Serializable
{
    private BigInteger notificationID;
    private Integer userID;
    private Integer sourceID;
    private boolean flag;
    private String topic;
    private String message;
    private String timestamp;
    private Integer priority;
    private Integer count;

    @Id
    @Column(name ="notificationid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public BigInteger getNotificationID()
    {
        return notificationID;
    }

    public void setNotificationID(BigInteger bigInteger)
    {
        this.notificationID = bigInteger;
    }

    @Column(name = "userid")
    public Integer getUserID()
    {
        return userID;
    }

    public void setUserID(Integer userID)
    {
        this.userID = userID;
    }

    @Column(name = "sourceid",nullable = false)
    public Integer getSourceID()
    {
        return sourceID;
    }

    public void setSourceID(Integer sourceID)
    {
        this.sourceID = sourceID;
    }

    @Column(name = "flag",nullable = false)
    public boolean isFlag()
    {
        return flag;
    }

    public void setFlag(boolean flag)
    {
        this.flag = flag;
    }

    @Column(name = "topic",nullable = false)
    public String getTopic()
    {
        return topic;
    }

    public void setTopic(String topic)
    {
        this.topic = topic;
    }

    @Column(name = "message")
    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Column(name = "time",nullable = false)
    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    @Column(name = "priority",nullable = false)
    public Integer getPriority()
    {
        return priority;
    }

    public void setPriority(Integer priority)
    {
        this.priority = priority;
    }
    
    @Column(name = "count",nullable = false)
    public Integer getCount()
    {
        return count;
    }

    public void setCount(Integer count)
    {
        this.count = count;
    }
}