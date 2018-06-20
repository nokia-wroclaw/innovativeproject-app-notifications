package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

	private static DatabaseConnector INSTANCE;
    private static Connection connection = null;
	
	public DatabaseConnector() {

        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://35.204.202.104:5432/AppNotifications",
                    "postgres",
                    "mysecretpassword");
            System.out.println("Successfully connected to database!");


        } catch (ClassNotFoundException e) {

            e.printStackTrace();
            System.out.append("No PostgreSQL library found, include library in project directory.");

        } catch (SQLException e) {

            e.printStackTrace();
            System.out.append("Database login failed! Check Your username and/or password.");

        }
    }
	
	/**
     * This method is a thread-safe Java singleton handler
     * It detects if new object should be created, or should it be retrieved from already existing instance
     *
     * @return an object of type [DatabaseConnection]
     */
    public static DatabaseConnector getInstance() {
        if (INSTANCE == null) {
            synchronized (DatabaseConnector.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DatabaseConnector();
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
