package com.example.mati.pojo;

public class AddWebsiteBody
{
    private String token;
    private String website;

    public AddWebsiteBody(String token, String website) {
        this.token = token;
        this.website = website;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
