package Factory;

import Database.DatabaseConnection;
import Model.Notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class NotificationFactory {

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    /**
     * Queries database for all users
     *
     * @return ArrayList of [User] object, containing all users stored in PostgreSQL database
     */
    public ArrayList<Notification> getNotifications() {

        ArrayList<Notification> listOfNotifications = new ArrayList<>();

        try {
            PreparedStatement sqlStatement = connection.prepareStatement("SELECT * FROM nokiaapp.notification");
            ResultSet sqlStatementResult = sqlStatement.executeQuery();
            while (sqlStatementResult.next()) {
                listOfNotifications.add(new Notification(
                        sqlStatementResult.getInt("NotificationID"),
                        sqlStatementResult.getInt("SourceID"),
                        sqlStatementResult.getInt("UserID"),
                        sqlStatementResult.getBoolean("Flag"),
                        sqlStatementResult.getString("Topic"),
                        sqlStatementResult.getString("Message"),
                        sqlStatementResult.getTimestamp("Time"),
                        sqlStatementResult.getInt("Priority")
                ));
            }

            sqlStatement.closeOnCompletion();

            return listOfNotifications;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return listOfNotifications;
    }

    public static ArrayList<Notification> getNotifications(Connection connection) {

    	ArrayList<Notification> listOfNotifications = new ArrayList<>();

        try {
            PreparedStatement sqlStatement = connection.prepareStatement("SELECT * FROM nokiaapp.notification");
            ResultSet sqlStatementResult = sqlStatement.executeQuery();
            while (sqlStatementResult.next()) {
                listOfNotifications.add(new Notification(
                		sqlStatementResult.getInt("NotificationID"),
                        sqlStatementResult.getInt("SourceID"),
                        sqlStatementResult.getInt("UserID"),
                        sqlStatementResult.getBoolean("Flag"),
                        sqlStatementResult.getString("Topic"),
                        sqlStatementResult.getString("Message"),
                        sqlStatementResult.getTimestamp("Time"),
                        sqlStatementResult.getInt("Priority")
                ));
            }

            sqlStatement.closeOnCompletion();

            return listOfNotifications;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return listOfNotifications;
    }

    /**
     * Stores notifiaction in PostgreSQL database
     *
     * @param notification [Notification] object to be stored in database
     */
    public void addNotification(Notification notification) {
        try {

            PreparedStatement sqlStatement = connection.prepareStatement("INSERT INTO nokiaapp.notification(NotificationID, UserID, SourceID, Flag, Topic, Message, Time, Priority) VALUES(?,?,?,?,?,?,?,?)");
            sqlStatement.setInt(1, notification.getNotificationID());
            sqlStatement.setInt(2, notification.getUserID());
            sqlStatement.setInt(3, notification.getSourceID());
            sqlStatement.setBoolean(4, notification.getFlag());
            sqlStatement.setString(5, notification.getTopic());
            sqlStatement.setString(6, notification.getMessage());
            sqlStatement.setTimestamp(7, notification.getTime());
            sqlStatement.setInt(8, notification.getPriority());
            sqlStatement.executeUpdate();
            sqlStatement.closeOnCompletion();

        } catch (SQLException sqlException) {

            sqlException.printStackTrace();

        }
    }

    /**
     * Queries SQL for countries, using country name
     *
     * @param countryName name of country to find
     * @return Null if not found, ArrayList<Country> object if found
     */
    public ArrayList<Notification> findNotificationByID(int id) {

        ArrayList<Notification> listOfNotifications = new ArrayList<>();

        try {
            PreparedStatement sqlStatement = connection.prepareStatement("SELECT * FROM nokiaapp.notification WHERE NotificationID = ?;");
            sqlStatement.setInt(1, id);
            ResultSet sqlStatementResult = sqlStatement.executeQuery();

            while (sqlStatementResult.next()) {
                listOfNotifications.add(new Notification(
                        sqlStatementResult.getInt("NotificationID"),
                        sqlStatementResult.getInt("UserID"),
                        sqlStatementResult.getInt("SourceID"),
                        sqlStatementResult.getBoolean("Flag"),
                        sqlStatementResult.getString("Topic"),
                        sqlStatementResult.getString("Message"),
                        sqlStatementResult.getTimestamp("Time"),
                        sqlStatementResult.getInt("Priority")
                ));
            }

            return listOfNotifications;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return null;
    }

    /**
     * Removes entity from table /notification.user/
     *
     * @param notification [Notification] object to remove from database
     */
    public void deleteNotification(Notification notification, Connection connection) {
        try {
            PreparedStatement sqlStatement = connection.prepareStatement(
                    "DELETE " +
                            "FROM nokiaapp.notification " +
                            "WHERE id = ?;");
            sqlStatement.setInt(1, notification.getNotificationID());
            sqlStatement.executeUpdate();
            sqlStatement.closeOnCompletion();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}