package com.example.mati.pojo;

public class User
{
    String name;
    String surname;
    String login;
    String password;

    public User()
    {
    }

    public User(String name, String surname, String login, String password)
    {
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
