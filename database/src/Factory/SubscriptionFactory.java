package Factory;

import Database.DatabaseConnection;
import Model.Subscription;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SubscriptionFactory {

    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    /**
     * Queries database for all users
     *
     * @return ArrayList of [User] object, containing all users stored in PostgreSQL database
     */
    public ArrayList<Subscription> getSubscriptions() {

        ArrayList<Subscription> listOfSubscriptions = new ArrayList<>();

        try {
            PreparedStatement sqlStatement = connection.prepareStatement("SELECT * FROM nokiaapp.subscription");
            ResultSet sqlStatementResult = sqlStatement.executeQuery();
            while (sqlStatementResult.next()) {
                listOfSubscriptions.add(new Subscription(
                        sqlStatementResult.getInt("UserID"),
                        sqlStatementResult.getInt("SourceID"),
                        sqlStatementResult.getString("Name")
                ));
            }

            sqlStatement.closeOnCompletion();

            return listOfSubscriptions;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return listOfSubscriptions;
    }

    public static ArrayList<Subscription> getSubscriptions(Connection connection) {

    	ArrayList<Subscription> listOfSubscriptions = new ArrayList<>();

        try {
            PreparedStatement sqlStatement = connection.prepareStatement("SELECT * FROM nokiaapp.subscription");
            ResultSet sqlStatementResult = sqlStatement.executeQuery();
            while (sqlStatementResult.next()) {
                listOfSubscriptions.add(new Subscription(
                		sqlStatementResult.getInt("UserID"),
                        sqlStatementResult.getInt("SourceID"),
                        sqlStatementResult.getString("Name")
                ));
            }

            sqlStatement.closeOnCompletion();

            return listOfSubscriptions;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return listOfSubscriptions;
    }

    /**
     * Stores country in PostgreSQL database
     *
     * @param country [Country] object to be stored in database
     */
    public void addSubscription(Subscription subscription, Connection connection) {
        try {

            PreparedStatement sqlStatement = connection.prepareStatement("INSERT INTO nokiaapp.subscription(UserID, SourceID, Name) VALUES(?,?,?)");
            sqlStatement.setInt(1, subscription.getUser());
            sqlStatement.setInt(2, subscription.getSource());
            sqlStatement.setString(3, subscription.getName());
            sqlStatement.executeUpdate();
            sqlStatement.closeOnCompletion();

        } catch (SQLException sqlException) {

            sqlException.printStackTrace();

        }
    }

    /**
     * Removes entity from table /notification.user/
     *
     * @param user [User] object to remove from database
     */
    public void deleteSubscription(Subscription subscription, Connection connection) {
        try {
            PreparedStatement sqlStatement = connection.prepareStatement(
                    "DELETE " +
                            "FROM nokiaapp.subscription " +
                            "WHERE UserID = ? AND SourceID = ?;");
            sqlStatement.setInt(1, subscription.getUser());
            sqlStatement.setInt(2, subscription.getSource());
            sqlStatement.executeUpdate();
            sqlStatement.closeOnCompletion();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}