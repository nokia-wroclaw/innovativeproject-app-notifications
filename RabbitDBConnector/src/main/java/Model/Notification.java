package Model;

import java.math.BigInteger;

public class Notification
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

    public BigInteger getNotificationID()
    {
        return notificationID;
    }

    public void setNotificationID(BigInteger notificationID)
    {
        this.notificationID = notificationID;
    }

    public Integer getUserID()
    {
        return userID;
    }

    public void setUserID(Integer userID)
    {
        this.userID = userID;
    }

    public Integer getSourceID()
    {
        return sourceID;
    }

    public void setSourceID(Integer sourceID)
    {
        this.sourceID = sourceID;
    }

    public boolean isFlag()
    {
        return flag;
    }

    public void setFlag(boolean flag)
    {
        this.flag = flag;
    }

    public String getTopic()
    {
        return topic;
    }

    public void setTopic(String topic)
    {
        this.topic = topic;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public Integer getPriority()
    {
        return priority;
    }

    public void setPriority(Integer priority)
    {
        this.priority = priority;
    }

    public Integer getCount(){ return count; }

    public void setCount(Integer count){ this.count = new Integer(count); }

}