package Connection;

import Model.Notification;
import com.rabbitmq.client.*;
import jdbctemplate.AccountJDBCTemplate;
import jdbctemplate.NotificationJDBCTemplate;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


class RabbitToDB{

    private ApplicationContext context;
    private NotificationJDBCTemplate notificationService;

    private final static String QUEUE_NAME = "DEMO_QUEUE";

    private String rabbitName = "RabbitMQ";

    private String SQLpassword;

    private Connection connection;
    private Channel channel;

    public void connectToQueue(){

        ConnectionFactory factory = new ConnectionFactory();

        factory.setPort(5672);
        //factory.setHost(rabbitName);

        factory.setHost("localhost");

        try {
            connection = factory.newConnection();
            System.out.println("Connected to Rabbit");

        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Connecting to the rabbit failed");
        }

    }

    public void receiveJSONs() throws IOException {

        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        Consumer consumer = new NotificationConsumer(channel);

        channel.basicConsume(QUEUE_NAME, false, consumer);
        System.out.println("Consumer created");
    }

    public void closeConnection() throws IOException, TimeoutException {
        System.out.println("Closing connections");

        channel.close();
        connection.close();
    }


    RabbitToDB() {

        context = new ClassPathXmlApplicationContext("Beans.xml");
        notificationService = (NotificationJDBCTemplate)context.getBean("NotificationJDBCTemplate");

        while (true) {

            if(connection == null || !connection.isOpen()) {
                    connectToQueue();
            }

            if (
                    (notificationService != null)&&
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
/*        if(argv.length==0 || argv[0]==null){

            System.out.println("not enough arguments");
            return;
        }*/

        new RabbitToDB();
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
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

            try {
                String message = new String(body, "UTF-8");
                WorkWithNotification(message);
            } finally {
                channel.basicAck(envelope.getDeliveryTag(), false);
            }

        }

        @Override
        public void handleShutdownSignal(String S,ShutdownSignalException e){
            super.handleShutdownSignal(S,e);
            e.printStackTrace();
            System.out.println("Shutting down consumer");
        }

        private void WorkWithNotification(String message){
            JSONObject JSON = new JSONObject(message);

            System.out.println("Receiving notification");
            Notification receivedNotification = new Notification();
            receivedNotification.setUserID(JSON.getInt("userID"));
            receivedNotification.setSourceID(JSON.getInt("sourceID"));
            receivedNotification.setTopic(JSON.getString("topic"));
            receivedNotification.setMessage(JSON.getString("message"));
            receivedNotification.setPriority(JSON.getInt("priority"));


            AggregationMethod method = AggregationMethod.Count;
            BigInteger ID;

            switch (method){
                case None:

                    notificationService.addNotification(receivedNotification);

                    break;

                case Count:
                    ID = notificationService.findNotificationByTopic(
                            receivedNotification.getTopic(), receivedNotification.getUserID(), receivedNotification.getSourceID()
                    );

                    if(ID.equals(BigInteger.valueOf(-1))){
                        notificationService.addNotification(receivedNotification);
                    }else {
                        notificationService.IncrementCount(receivedNotification.getCount(), ID);
                    }

                    break;

                case Last:

                    ID = notificationService.findNotificationByTopic(
                            receivedNotification.getTopic(), receivedNotification.getUserID(), receivedNotification.getSourceID()
                    );

                    if(ID.equals(BigInteger.valueOf(-1))){
                        notificationService.addNotification(receivedNotification);
                    }else {
                        notificationService.updateNotification(receivedNotification,ID);
                    }

                    break;

                case First:

                    ID = notificationService.findNotificationByTopic(
                            receivedNotification.getTopic(), receivedNotification.getUserID(), receivedNotification.getSourceID()
                    );

                    if(ID.equals(BigInteger.valueOf(-1))){
                        notificationService.addNotification(receivedNotification);
                    }else {}

                    break;
            }
        }
    }

}

