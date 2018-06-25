package com.example.mati.pojo;

public class AddServiceBody
{
    private String token;
    private String pin;

    public AddServiceBody(String token, String pin) {
        this.token = token;
        this.pin = pin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
