package dao;

import java.util.List;
import Model.User;

public interface UserDAO
{
    public User getUser(Integer id);
    public User getUserByLogin(String login);
    public User getUserByToken(String userToken);
    public List<User> listUsers();
    public void createUser(User user);
    public void changePassword(Integer id, String newPassword);
    public void createToken(Integer id, String token);
    public void removeUser(Integer id);
}
