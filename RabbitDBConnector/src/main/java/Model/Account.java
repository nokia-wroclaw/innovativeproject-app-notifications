package Model;

public class Account
{
    private Integer accountID;
    private Integer userID;
    private String login;
    private String password;
    private String accessToken;
    private String accessTokenSecret;
    private Integer sourceID;
    private Integer aggregation;
    private Integer aggregationdate;
    private Integer aggregationtype;
    private String aggregationkey;

    public Integer getAccountID()
    {
        return accountID;
    }

    public void setAccountID(Integer accountID)
    {
        this.accountID = accountID;
    }

    public Integer getUserID()
    {
        return userID;
    }

    public void setUserID(Integer userID)
    {
        this.userID = userID;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    public String getAccessTokenSecret()
    {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret)
    {
        this.accessTokenSecret = accessTokenSecret;
    }

	public Integer getSourceID() {
		return sourceID;
	}

	public void setSourceID(Integer sourceID) {
		this.sourceID = sourceID;
	}

	public Integer getAggregation() {
		return aggregation;
	}

	public void setAggregation(Integer aggregation) {
		this.aggregation = aggregation;
	}

	public Integer getAggregationdate() {
		return aggregationdate;
	}

	public void setAggregationdate(Integer aggregationdate) {
		this.aggregationdate = aggregationdate;
	}

	public Integer getAggregationtype() {
		return aggregationtype;
	}

	public void setAggregationtype(Integer aggregationtype) {
		this.aggregationtype = aggregationtype;
	}

	public String getAggregationkey() {
		return aggregationkey;
	}

	public void setAggregationkey(String aggregationkey) {
		this.aggregationkey = aggregationkey;
	}
}
