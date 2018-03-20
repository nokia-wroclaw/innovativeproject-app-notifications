import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.json.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class NotificationGenerator {

    private final static String QUEUE_NAME = "JSON_canal";

    private JSONObject notificationJSON;

    private Connection connection;
    private Channel channel;

    private String readFile(String path) throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, Charset.defaultCharset());
    }

    NotificationGenerator() throws IOException, TimeoutException, InterruptedException {

        connectToQueue();
        sendJSON();
        TimeUnit.SECONDS.sleep(1);
 /*       sendJSON();
        TimeUnit.SECONDS.sleep(1);
        sendJSON();
        TimeUnit.SECONDS.sleep(1);
        sendJSON();
        TimeUnit.SECONDS.sleep(1);
        sendJSON();*/

        closeConnection();
    }


    public void connectToQueue() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    }

    public void sendJSON() throws IOException {

        String stringJSON = readFile("./src/main/resources/message.json");

        notificationJSON = new JSONObject(stringJSON);

        Date time =  Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date1 = format.format(time);

        notificationJSON.put("timeStamp",date1);
        stringJSON = notificationJSON.toString();

        channel.basicPublish("", QUEUE_NAME, null, stringJSON.getBytes());

        System.out.println(" -+- Sent: " + notificationJSON.getString("topic"));

    }

    public void closeConnection() throws IOException, TimeoutException {

        channel.close();
        connection.close();
    }


    public static void main(String[] argv) throws java.io.IOException, java.util.concurrent.TimeoutException, InterruptedException {
        new NotificationGenerator();
    }

}
