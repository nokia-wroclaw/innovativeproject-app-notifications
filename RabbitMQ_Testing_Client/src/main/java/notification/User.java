package notification;

public class User {
    private String name;
    private String surname;
    private String login;
    private String password;

    public String getName(){
        return name;
    }

    public String getSurname(){
        return surname;
    }

    public String getLogin(){
        return login;
    }

    public String getPassword(){
        return password;
    }

    @Override
    public String toString(){
        return "" +
                name + " " + surname;

    }
}
