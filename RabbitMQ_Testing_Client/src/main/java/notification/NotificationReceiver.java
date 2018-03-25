package notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;


public class NotificationReceiver extends Thread{
    private final static String QUEUE_NAME = "JSON_canal";

    private Connection connection;
    private Channel channel;

    private NotificationManager notifications;

   public NotificationReceiver(NotificationManager manager){

        notifications = manager;
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
                ObjectMapper mapper = new ObjectMapper();

                try {
                    Notification receivedNotification = mapper.readValue(message, Notification.class);
                    System.out.println(receivedNotification.getTopic());
                    notifications.addNotification(receivedNotification);

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

    @Override
    public void run(){
        super.run();

        try {
            connectToQueue();
            receiveJSONs();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
