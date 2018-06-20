package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitControll {

	private String QUEUE_NAME = "DEMO_QUEUE";
    private String queueString;
    private JSONObject lastQueueJSON;
    private Connection connection;
    private Channel channel;
    
    public RabbitControll() {
    	System.out.println("Creating new instance of RabbitController.");
    }
    
    private static boolean doubleCheck(JSONObject lastQueueJSON, JSONObject notificationJSON) throws JSONException{
        if (lastQueueJSON.getString("message").equals(notificationJSON.getString("message"))&&
                lastQueueJSON.getString("topic").equals(notificationJSON.getString("topic"))&&
                lastQueueJSON.getString("time").substring(0, 17).equals(notificationJSON.getString("time").substring(0,17))&&
                lastQueueJSON.get("userID").equals(notificationJSON.get("userID")))
        {
            return false;
        }
        else return true;
    }

    private static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = Calendar.getInstance().getTime();
        return sdfDate.format(now);
    }

    public void generateNote(String topic, String message, int userID) throws IOException, TimeoutException, JSONException {
    	org.json.JSONObject notificationJSON;
        notificationJSON = new org.json.JSONObject();
        notificationJSON.put("topic", topic);
        notificationJSON.put("message", message);
        notificationJSON.put("sourceID", 1);
        notificationJSON.put("time", getCurrentTimeStamp());
        notificationJSON.put("priority", 0);
        notificationJSON.put("userID", userID);

        queueString = notificationJSON.toString();
        
        connectToQueue();
        sendJSON();
        closeConnection();
        lastQueueJSON = notificationJSON;
    }
    
    public void generateNote(String topic, String message, int userID, String time) throws IOException, TimeoutException, JSONException {
        org.json.JSONObject notificationJSON;
        notificationJSON = new org.json.JSONObject();
        notificationJSON.put("topic", topic);
        notificationJSON.put("message", message);
        notificationJSON.put("sourceID", 1);
        notificationJSON.put("time", time);
        notificationJSON.put("priority", 0);
        notificationJSON.put("userID", userID);

        queueString = notificationJSON.toString();
        
        connectToQueue();
        sendJSON();
        closeConnection();
    }

    private void connectToQueue() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(5672);
        factory.setHost("35.204.202.104");

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
    }

    private void closeConnection() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    private void sendJSON() throws IOException {
        channel.basicPublish("", QUEUE_NAME, null, queueString.getBytes());
    }
}
