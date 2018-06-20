package mapper;

import Model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class UserMapper implements RowMapper<User>
{
    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException
    {
        User user =new User();
        user.setUserId(resultSet.getInt("userid"));
        user.setName(resultSet.getString("name"));
        user.setSurname(resultSet.getString("surname"));
        user.setLogin(resultSet.getString("login"));
        user.setPassword(resultSet.getString("password"));
        user.setToken(resultSet.getString("token"));
        return user;
    }
}