package jdbctemplate;

import model.User;
import dao.UserDAO;
import mapper.UserMapper;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserJDBCTemplate implements UserDAO
{
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    @Override
    public User getUser(Integer id)
    {
        String SQL = "select * from nokiaapp.user where userid = ?";
        User user = jdbcTemplateObject.queryForObject(SQL, new Object[]{id}, new UserMapper());
        return user;
    }

    @Override
    public User getUserByLogin(String login)
    {
        String SQL = "select * from nokiaapp.user where login = ?";
        User user = jdbcTemplateObject.queryForObject(SQL, new Object[]{login}, new UserMapper());
        return user;
    }

    @Override
    public User getUserByToken(String userToken)
    {
        String SQL = "select * from nokiaapp.user where token = ?";
        User user = jdbcTemplateObject.queryForObject(SQL, new Object[]{userToken}, new UserMapper());
        return user;
    }

    @Override
    public List<User> listUsers()
    {
        String SQL = "select * from nokiaapp.user";
        List <User> students = jdbcTemplateObject.query(SQL, new UserMapper());
        return students;
    }

    @Override
    public void createUser(User user)
    {
        String SQL = "INSERT INTO nokiaapp.user (Name, Surname, Login, Password, Token) VALUES (?,?,?,?,?)";
        jdbcTemplateObject.update(SQL, user.getName(),user.getSurname(),user.getLogin(),user.getPassword(),user.getToken());
    }

    @Override
    public void changePassword(Integer id, String newPassword)
    {
        String SQL = "update nokiaapp.user set password = ? where userid = ?";
        jdbcTemplateObject.update(SQL, newPassword, id);
    }

    @Override
    public void createToken(Integer id, String token)
    {
        String SQL = "update nokiaapp.user set token = ? where userid = ?";
        jdbcTemplateObject.update(SQL, token, id);
    }

    @Override
    public void removeUser(Integer id)
    {
        String SQL = "delete from nokiaapp.user where userid = ?";
        jdbcTemplateObject.update(SQL, id);
    }
}