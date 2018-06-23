package com.example.mati.pojo;

public class RemoveAccountBody {
    private String token;
    private String oldpassword;

    public RemoveAccountBody(String token, String oldpassword)
    {
        this.token = token;
        this.oldpassword = oldpassword;
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

}
