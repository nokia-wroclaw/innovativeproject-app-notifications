package Model;

public class Subscription
{
    private Integer userID;
    private Integer sourceID;
    private String name;

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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}