import com.rabbitmq.client.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class NotificationReceiver {
    private final static String QUEUE_NAME = "JSON_canal";

    private ArrayList<JSONObject> notificationsJSON;

    private Connection connection;
    private Channel channel;

    NotificationReceiver() throws IOException, TimeoutException, InterruptedException {

        notificationsJSON = new ArrayList<JSONObject>();
        connectToQueue();
        receiveJSONs();

        while( System.in.read() != 'q'){ TimeUnit.MICROSECONDS.sleep(10);}
        closeConnection();

    }

    public void connectToQueue() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" -+- Waiting for messages (type 'q' to exit) : \n");
    }

    public void receiveJSONs() throws IOException {

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws java.io.UnsupportedEncodingException {
                String message = new String(body, "UTF-8");

                JSONObject tmpNot = new JSONObject(message);
                notificationsJSON.add(tmpNot);

                System.out.println(" -+- Received: " + tmpNot.getString("topic"));

            }
            @Override
            public void handleShutdownSignal(String S,ShutdownSignalException e){
                System.out.println(" shutting down consumer ");

            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

    public void closeConnection() throws IOException, TimeoutException {

        channel.close();
        connection.close();
    }

    public static void main(String[] argv) throws java.io.IOException, java.util.concurrent.TimeoutException, InterruptedException {
        new NotificationReceiver();
    }

}
