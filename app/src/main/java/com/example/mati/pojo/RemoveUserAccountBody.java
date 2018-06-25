package com.example.mati.pojo;

public class RemoveUserAccountBody
{
    private String token;
    private String source;
    private String accesstoken;

    public RemoveUserAccountBody(String token, String source, String accesstoken) {
        this.token = token;
        this.source = source;
        this.accesstoken = accesstoken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }
}
