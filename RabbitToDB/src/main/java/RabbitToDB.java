import Database.DatabaseConnection;
import Factory.NotificationFactory;
import Model.Notification;
import com.rabbitmq.client.*;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;


class RabbitToDB{

    private final static String QUEUE_NAME = "DEMO_QUEUE";
    private DatabaseConnection database;
    private NotificationFactory notificationFactory;

    private String rabbitName = "RabbitMQ";

    private String SQLpassword;

    private Connection connection;
    private Channel channel;

    public void connectToQueue(){


        ConnectionFactory factory = new ConnectionFactory();

        factory.setPort(5672);
        factory.setHost(rabbitName);

        //factory.setHost("35.204.202.104");

        //factory.setHost("localhost");

        try {
            connection = factory.newConnection();
            System.out.println("Connected to Rabbit");

        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Connecting to the rabbit failed");
        }

    }

    public void connectToDatabase(){
        database = new DatabaseConnection(SQLpassword,"postgres");
        if(database.getConnection()!=null){
            notificationFactory = new NotificationFactory();
            System.out.println("Connected to data base");
        }
    }

    public void receiveJSONs() throws IOException {

        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        Consumer consumer = new NotificationConsumer(channel);

        channel.basicConsume(QUEUE_NAME, true, consumer);
        System.out.println("Consumer created");
    }

    public void closeConnection() throws IOException, TimeoutException {
        System.out.println("Closing connections");

        channel.close();
        connection.close();
    }


    RabbitToDB(String password) {

        SQLpassword = password;

        while (true) {

            if(connection == null || !connection.isOpen()) {
                    connectToQueue();
            }

            if(database == null || database.getConnection() == null) {
                connectToDatabase();
            }

            if (
                    (database != null && database.getConnection() != null)&&
                    ( connection!=null && connection.isOpen() )&&
                    ( channel==null || !channel.isOpen() )
                ){

                try {
                    receiveJSONs();
                } catch (IOException e) {
                    //e.printStackTrace();
                    System.out.println("Creating rabbit client failed");
                }

            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] argv) {
        if(argv.length==0 || argv[0]==null){

            System.out.println("not enough arguments");
            return;
        }

        new RabbitToDB(argv[0]);
    }



    class NotificationConsumer extends DefaultConsumer {
        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public NotificationConsumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws UnsupportedEncodingException {
            String message = new String(body, "UTF-8");
            JSONObject JSON = new JSONObject(message);

            Notification receivedNotification = new Notification(
                    JSON.getInt("sourceID"),
                    JSON.getString("topic"),
                    JSON.getString("message"),
                    null,
                    JSON.getInt("priority"));

            notificationFactory.addNotification(receivedNotification);

            System.out.println(receivedNotification.getTopic());


        }

        @Override
        public void handleShutdownSignal(String S,ShutdownSignalException e){
            super.handleShutdownSignal(S,e);
            e.printStackTrace();
            System.out.println("Shutting down consumer");
        }
    }
}

