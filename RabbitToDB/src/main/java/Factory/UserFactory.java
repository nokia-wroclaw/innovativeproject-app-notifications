package Factory;

import Database.DatabaseConnection;
import Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserFactory {

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    /**
     * Queries database for all users
     *
     * @return ArrayList of [User] object, containing all users stored in PostgreSQL database
     */
    public ArrayList<User> getUsers() {

        ArrayList<User> listOfUsers = new ArrayList<>();

        try {
            PreparedStatement sqlStatement = connection.prepareStatement("SELECT * FROM nokiaapp.user");
            ResultSet sqlStatementResult = sqlStatement.executeQuery();
            while (sqlStatementResult.next()) {
                listOfUsers.add(new User(
                		sqlStatementResult.getInt("UserID"),
                        sqlStatementResult.getString("Name"),
                        sqlStatementResult.getString("Surname"),
                        sqlStatementResult.getString("Login"),
                        sqlStatementResult.getString("Password")
                ));
            }

            sqlStatement.closeOnCompletion();

            return listOfUsers;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return listOfUsers;
    }

    public static ArrayList<User> getUsers(Connection connection) {

    	ArrayList<User> listOfUsers = new ArrayList<>();

        try {
            PreparedStatement sqlStatement = connection.prepareStatement("SELECT * FROM nokiaapp.user");
            ResultSet sqlStatementResult = sqlStatement.executeQuery();
            while (sqlStatementResult.next()) {
                listOfUsers.add(new User(
                        sqlStatementResult.getInt("UserID"),
                        sqlStatementResult.getString("Name"),
                        sqlStatementResult.getString("Surname"),
                        sqlStatementResult.getString("Login"),
                        sqlStatementResult.getString("Password")
                ));
            }

            sqlStatement.closeOnCompletion();

            return listOfUsers;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return listOfUsers;
    }

    /**
     * Stores country in PostgreSQL database
     *
     */
    public void addUser(User user,Connection connection) {
        try {

            PreparedStatement sqlStatement = connection.prepareStatement("INSERT INTO nokiaapp.user(UserID, Name, Surname, Login, Password) VALUES(?,?,?,?,?)");
            sqlStatement.setInt(1, user.getId());
            sqlStatement.setString(2, user.getName());
            sqlStatement.setString(3, user.getSurname());
            sqlStatement.setString(4, user.getLogin());
            sqlStatement.setString(5, user.getPassword());
            sqlStatement.executeUpdate();
            sqlStatement.closeOnCompletion();

        } catch (SQLException sqlException) {

            sqlException.printStackTrace();

        }
    }

    /**
     * Queries SQL for countries, using country name
     *
     * @return Null if not found, ArrayList<Country> object if found
     */
    public ArrayList<User> findUserBySurname(String surname, Connection connection) {

        ArrayList<User> listOfUsers = new ArrayList<>();

        try {
            PreparedStatement sqlStatement = connection.prepareStatement("SELECT * FROM nokiaapp.user WHERE Surname = ?;");
            sqlStatement.setString(1, surname);
            ResultSet sqlStatementResult = sqlStatement.executeQuery();

            while (sqlStatementResult.next()) {
                listOfUsers.add(new User(
                        sqlStatementResult.getInt("UserID"),
                        sqlStatementResult.getString("Name"),
                        sqlStatementResult.getString("Surname"),
                        sqlStatementResult.getString("Login"),
                        sqlStatementResult.getString("Password")
                ));
            }

            return listOfUsers;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return null;
    }

    /**
     * Removes entity from table /notification.user/
     *
     * @param user [User] object to remove from database
     */
    public void deleteUser(User user) {
        try {
            PreparedStatement sqlStatement = connection.prepareStatement(
                    "DELETE " +
                            "FROM nokiaapp.user " +
                            "WHERE id = ? AND name = ? AND surname = ?;");
            sqlStatement.setInt(1, user.getId());
            sqlStatement.setString(2, user.getName());
            sqlStatement.setString(3, user.getPassword());
            sqlStatement.executeUpdate();
            sqlStatement.closeOnCompletion();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}