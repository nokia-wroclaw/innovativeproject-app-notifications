import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;


public class NotificationReceiver {
    private final static String QUEUE_NAME = "JSON_canal";

    private ArrayList<Notification> notifications;

    private Connection connection;
    private Channel channel;

    NotificationReceiver() throws IOException, TimeoutException, InterruptedException {

        notifications = new ArrayList<Notification>();
        connectToQueue();
        receiveJSONs();
        char sysIn;

        do{
            sysIn = (char) System.in.read();
            if(sysIn == 'd' && !(notifications.isEmpty())){
                System.out.println(notifications.get(notifications.size()-1));
            }
        }
        while( sysIn != 'q');

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
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws UnsupportedEncodingException {
                String message = new String(body, "UTF-8");

                //JSONObject tmpNot = new JSONObject(message);
                //notificationsJSON.add(tmpNot);

                //System.out.println(" -+- Received: " + tmpNot.getString("topic"));

                ObjectMapper mapper = new ObjectMapper();

                try {
                    Notification notification = mapper.readValue(message, Notification.class);
                    notifications.add(notification);
                    System.out.println(" -+- Received: " + notification.getTopic()  + " \'d\' for detail ");

                } catch (IOException e) {
                    e.printStackTrace();
                }


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
