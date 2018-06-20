package com.example.mati.pojo;

public class ChangeFlagBody
{
    private String token;
    private String notfid;

    public ChangeFlagBody(String token, String notfid)
    {
        this.token = token;
        this.notfid = notfid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNotfid() {
        return notfid;
    }

    public void setNotfid(String notfid) {
        this.notfid = notfid;
    }
}
