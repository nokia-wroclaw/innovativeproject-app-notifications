package com.example.mati.pojo;

public class AggregationBody
{
    private String token;
    private String account;
    private String aggregation;
    private String aggregationdate;
    private String aggregationtype;
    private String aggregationby;
    private String aggregationkey;

    public AggregationBody(String token, String account, String aggregation, String aggregationdate, String aggregationtype, String aggregationby, String aggregationkey) {
        this.token = token;
        this.account = account;
        this.aggregation = aggregation;
        this.aggregationdate = aggregationdate;
        this.aggregationtype = aggregationtype;
        this.aggregationby = aggregationby;
        this.aggregationkey = aggregationkey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAggregation() {
        return aggregation;
    }

    public void setAggregation(String aggregation) {
        this.aggregation = aggregation;
    }

    public String getAggregationdate() {
        return aggregationdate;
    }

    public void setAggregationdate(String aggregationdate) {
        this.aggregationdate = aggregationdate;
    }

    public String getAggregationtype() {
        return aggregationtype;
    }

    public void setAggregationtype(String aggregationtype) {
        this.aggregationtype = aggregationtype;
    }

    public String getAggregationby() {
        return aggregationby;
    }

    public void setAggregationby(String aggregationby) {
        this.aggregationby = aggregationby;
    }

    public String getAggregationkey() {
        return aggregationkey;
    }

    public void setAggregationkey(String aggregationkey) {
        this.aggregationkey = aggregationkey;
    }
}
