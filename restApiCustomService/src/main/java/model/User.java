package model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name ="nokiaapp.user")
public class User implements Serializable
{
    private Integer userId;
    private String name;
    private String surname;
    private String login;
    private String password;
    private String token;

    public  User()
    {}

    public User(Integer userId, String name, String surname, String login, String password, String token)
    {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.password = password;
        this.token = token;
    }

    @Id
    @Column(name ="userid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    @Column(name = "name",nullable = false)
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Column(name = "surname",nullable = false)
    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    @Column(name = "login",nullable = false)
    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    @Column(name = "password",nullable = false)
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Column(name = "token")
    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }
}
