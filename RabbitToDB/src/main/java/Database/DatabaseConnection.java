package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import Model.User;
import Factory.UserFactory;
/**
 * This singleton class stores connection to PostgreSQL database
 * Do not create new objects of this class, use getInstance() method instead!
 */
public class DatabaseConnection {

    private static DatabaseConnection INSTANCE;
    private static Connection connection = null;
    private String DBName = "PostgreSQL";// "35.204.202.104";

    private static String user;
    private static String password;


    /**
     * Constructor creates and keeps alive database connection
     * Connection is later stored in instance of this class
     */

    public DatabaseConnection(String password, String username) {

        DatabaseConnection.user=username;
        DatabaseConnection.password=password;

        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://"+DBName+":5432/AppNotifications",
                    username,
                    password);

            System.out.println("Successfully connected to database!");

        } catch (ClassNotFoundException e) {

            //e.printStackTrace();
            System.out.append("No PostgreSQL library found, include library in project directory.");

        } catch (SQLException e) {

            //e.printStackTrace();
            System.out.append("Database login failed! Check Your username and/or password.");

        }
    }

    /**
     * This method is a thread-safe Java singleton handler
     * It detects if new object should be created, or should it be retrieved from already existing instance
     *
     * @return an object of type [DatabaseConnection]
     */
    public static DatabaseConnection getInstance() {
        if (INSTANCE == null) {
            synchronized (DatabaseConnection.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DatabaseConnection(password,user);
                    System.out.println("Database connection instance not found, created new one.");
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Required by database factory to call statements
     *
     * @return [Connection] object created by this class constructor
     */
    public Connection getConnection() {
        return connection;
    }

}
