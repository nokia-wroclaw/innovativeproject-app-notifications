package Model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name ="nokiaapp.account")
public class Account implements Serializable
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
    private String aggregationkey;
    private Integer aggregationtype;

    @Id
    @Column(name ="accountid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getAccountID()
    {
        return accountID;
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

    public void setAccountID(Integer accountID)
    {
        this.accountID = accountID;
    }

    @Column(name = "login")
    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    @Column(name = "password")
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Column(name = "accesstoken")
    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    @Column(name = "accesstokensecret")
    public String getAccessTokenSecret()
    {
        return accessTokenSecret;
    }

    @Column(name = "sourceid")
    public Integer getSourceID()
    {
        return sourceID;
    }

    public void setSourceID(Integer sourceID)
    {
        this.sourceID = sourceID;
    }

    public void setAccessTokenSecret(String accessTokenSecret)
    {
        this.accessTokenSecret = accessTokenSecret;
    }

	@Column(name = "aggregation")
    public Integer getAggregation()
    {
        return aggregation;
    }

    public void setAggregation(Integer aggregation)
    {
        this.aggregation = aggregation;
    }

    @Column(name = "aggregationdate")
	public Integer getAggregationdate() {
		return aggregationdate;
	}

	public void setAggregationdate(Integer aggregationdate) {
		this.aggregationdate = aggregationdate;
	}

	public String getAggregationkey() {
		return aggregationkey;
	}

	public void setAggregationkey(String aggregationkey) {
		this.aggregationkey = aggregationkey;
	}

	public Integer getAggregationtype() {
		return aggregationtype;
	}

	public void setAggregationtype(Integer aggregationtype) {
		this.aggregationtype = aggregationtype;
	}
}
