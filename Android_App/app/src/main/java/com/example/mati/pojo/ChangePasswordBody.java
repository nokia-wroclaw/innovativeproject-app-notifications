package com.example.mati.pojo;

public class ChangePasswordBody
{
    private String token;
    private String oldpassword;
    private String newpassword;

    public ChangePasswordBody(String token, String oldpassword, String newpassword)
    {
        this.token = token;
        this.oldpassword = oldpassword;
        this.newpassword = newpassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOldpassword() {
        return oldpassword;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }
}
