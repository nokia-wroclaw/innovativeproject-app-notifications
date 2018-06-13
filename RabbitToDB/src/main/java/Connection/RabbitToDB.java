package Connection;

import Model.Account;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class RabbitToDB{

    private static ApplicationContext context;
    private static NotificationJDBCTemplate notificationService;
    private static AccountJDBCTemplate accountService;

    private final static String QUEUE_NAME = "DEMO_QUEUE";

    private String rabbitName = "RabbitMQ";

    private String SQLpassword;

    private Connection connection;
    private Channel channel;

    public void connectToQueue(){

        ConnectionFactory factory = new ConnectionFactory();

        factory.setPort(5672);
        //factory.setHost(rabbitName);

        factory.setHost("35.204.202.104");

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


    public RabbitToDB() {
        context = new ClassPathXmlApplicationContext("Beans.xml");
        notificationService = (NotificationJDBCTemplate)context.getBean("NotificationJDBCTemplate");
        accountService = (AccountJDBCTemplate)context.getBean("AccountJDBCTemplate");
    }
    
    public void monitoring() {
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

    public static void main(String[] args) {
		RabbitToDB app = new RabbitToDB();
		app.monitoring();
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
        	Notification receivedNotification = new Notification();
        	JSONObject JSON = null;
        	try {
        		 JSON = new JSONObject(message);
                
                receivedNotification.setUserID(JSON.getInt("userID"));
                receivedNotification.setSourceID(JSON.getInt("sourceID"));
                receivedNotification.setTopic(JSON.getString("topic"));
                receivedNotification.setMessage(JSON.getString("message"));
                receivedNotification.setPriority(JSON.getInt("priority"));
        	} catch(Exception e) {
        		System.out.println("Error occured while reading JSONObject object. Exception:\n" + e.getMessage());
        	}
        	
        	AggregationMethod method;
        	String websiteUrl = null;
        	//int aggregationMethod = -1;
        	int aggregationDate = -1;
        	List<Account> accounts = null;
        	Account account = null;
        	try {
        		//jesli source ID to Twitter
        		if(JSON.getInt("sourceID") == 15) {
        			//pobieramy dla uzytkownika wszystkie konta twiterowskie, powinno to byc jedno konto
        			accounts = accountService.AccountUserSourceList(JSON.getInt("userID"), JSON.getInt("sourceID"));
        			//jesli jest wiecej niz jedno konto lub nie ma kont to typ jest NONE
        			if(accounts.size() > 1 || accounts.size() == 0) {
        				System.out.println("Wrong size of twitter account list.");
        				method = AggregationMethod.None;
        			} // jesli jest pobrane jedno konto pobieramy typ agregacji i date agregacji 
        			else {
        				method = getAggregationType(accounts.get(0).getAggregation());
        				aggregationDate = accounts.get(0).getAggregationdate();
        				account  = accounts.get(0);
        			}
        		}
        		//jesli source ID to Website
        		else if(JSON.getInt("sourceID") == 10) {
        			//pobieramy strone, bo ona siedzi w accesstoken konta
        			websiteUrl = JSON.getString("topic").split("Zmiana na stronie ")[1];
        			System.out.println(websiteUrl);
        			accounts = accountService.getAccountUserSource(JSON.getInt("userID"), JSON.getInt("sourceID"), websiteUrl);
        			if(accounts.size() > 1 || accounts.size() == 0) {
        				System.out.println("Wrong size of website account list.");
        				method = AggregationMethod.None;
        			} // jesli jest pobrane jedno konto pobieramy typ agregacji i date agregacji 
        			else {
        				method = getAggregationType(accounts.get(0).getAggregation());
        				aggregationDate = accounts.get(0).getAggregationdate();
        				account  = accounts.get(0);
        			}
        		}
        		//jesli source ID inne
        		else {
        			method = AggregationMethod.None;
        			account  = null;
        			aggregationDate = -1;
        		}
        	} catch(Exception e) {
        		method = AggregationMethod.None;
        		account  = null;
    			aggregationDate = -1;
    			System.out.println("Error occured while getting aggregation type.");
    			System.out.println("Exception:\n" + e.getLocalizedMessage() + "\n" + e.getMessage());
        	}
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            BigInteger ID;
            
            Date notiDate;
            //System.out.println("Przed switchem.");
            //tutaj dopisujemy kod ktory zajmie sie sprawdzeniem czy ostatnia notyfikacja o takim timestampie jest w bazie czy nie i jesli tak to dodajemy jesli nie to nie hehe
            switch (method){
                case None:
                	try {
                		notificationService.addNotification(receivedNotification);
                	} catch(Exception e) {
                		System.out.println("Problem occured while adding notification with agregation method: NONE");
                	}

                    break;

                case Count:
                	try {
                		System.out.println("Agregation type - COUNT");
                		//Po czym agregujemy
                		if (account.getAggregationtype() == 1) {
                			//czy sa zdefiniowane key words
                			if(account.getAggregationkey().equals("-") || account.getAggregationkey().length() == 0) {
                				System.out.println("Searching notification by topic: " + receivedNotification.getTopic() +" "+ receivedNotification.getUserID() +" "+ receivedNotification.getSourceID());
                        		ID = notificationService.findNotificationByTopic(
                                        receivedNotification.getTopic(), receivedNotification.getUserID(), receivedNotification.getSourceID()
                                );
                			} else {
                				System.out.println("Searching notification by topic contains: " + account.getAggregationkey() +" "+ receivedNotification.getUserID() +" "+ receivedNotification.getSourceID());
                        		ID = notificationService.findNotificationByTopicContains(
                                        account.getAggregationkey(), receivedNotification.getUserID(), receivedNotification.getSourceID()
                                );
                			}
                			
                		} else if(account.getAggregationtype() == 2) {
                			if(account.getAggregationkey().equals("-") || account.getAggregationkey().length() == 0) {
                				System.out.println("Searching notification by message: " + receivedNotification.getMessage() +" "+ receivedNotification.getUserID() +" "+ receivedNotification.getSourceID());
                        		ID = notificationService.findNotificationByMessage(
                                        receivedNotification.getMessage(), receivedNotification.getUserID(), receivedNotification.getSourceID()
                                );
                			} else {
                				System.out.println("Searching notification by message contains: " + account.getAggregationkey() +" "+ receivedNotification.getUserID() +" "+ receivedNotification.getSourceID());
                        		ID = notificationService.findNotificationByMessageContains(
                        				account.getAggregationkey(), receivedNotification.getUserID(), receivedNotification.getSourceID()
                                );
                			}
                		} else {
                    		//jezeli jest nieagregowane po niczym to z definicji topic
                    		System.out.println("Searching notification by topic: " + receivedNotification.getTopic() +" "+ receivedNotification.getUserID() +" "+ receivedNotification.getSourceID());
                    		ID = notificationService.findNotificationByTopic(
                                  receivedNotification.getTopic(), receivedNotification.getUserID(), receivedNotification.getSourceID()
                            );
                		}
                		
                		System.out.println("Found notification. ID: " + ID);
                		Integer count = 0;
                		try {
                			count = notificationService.getNotification(ID).getCount();
                		} catch (Exception e) {
                			count  = -1;
                			System.out.println("First time seeing notification like this");
                		}
                		
                		System.out.println("count: " + count);
                        if(count == -1 || count == null){
                        	receivedNotification.setCount(0);
                            notificationService.addNotification(receivedNotification);
                        }else {
                        	//przy ikrementacji trzeba sprawdzac
                        	notiDate = sdf.parse(notificationService.getNotification(ID).getTimestamp());
                        	Date now = new Date();
                        	notiDate = new Date(notiDate.getTime() + account.getAggregationdate() * 3600000);
                        	System.out.println(now);
                        	System.out.println(notiDate);
                        	if(now.after(notiDate)) {
                        		System.out.println("Adding notification to database. Aggregation is outdated.");
                        		notificationService.addNotification(receivedNotification);
                        	} else {
                        		System.out.println("Aggregation is up to date. Increment counter.");
                        		notificationService.IncrementCount(count, ID);
                        	}
                            
                        	
                        }
                	} catch(Exception e) {
                		System.out.println("Problem occured while adding notification with agregation method: COUNT");
                		System.out.println("Exception:\n" + e.getMessage() + "\n" + e);
                	}

                    break;

                case Last:
                	try {
                		ID = notificationService.findNotificationByTopic(
                				receivedNotification.getTopic(), receivedNotification.getUserID(), receivedNotification.getSourceID()
                        );
                		
                        if(ID.equals(BigInteger.valueOf(-1))){
                            notificationService.addNotification(receivedNotification);
                        }else {
                        	notiDate = sdf.parse(notificationService.getNotification(ID).getTimestamp());
                        	Date now = new Date();
                        	notiDate = new Date(notiDate.getTime() + account.getAggregationdate() * 3600000);
                        	if(now.after(notiDate)) {
                        		System.out.println("Adding notification to database. Aggregation is outdated.");
                        		notificationService.addNotification(receivedNotification);
                        	} else {
                        		System.out.println("Aggregation is up to date. Actualising notification.");
                        		notificationService.updateNotification(receivedNotification,ID);
                        	}
                        }
                	} catch(Exception e) {
                		System.out.println("Problem occured while adding notification with agregation method: LAST");
                		System.out.println("Exception:\n" + e.getMessage() + "\n" + e);
                	}

                    break;

                case First:
                	try {
                		ID = notificationService.findNotificationByTopic(
                                receivedNotification.getTopic(), receivedNotification.getUserID(), receivedNotification.getSourceID()
                        );

                        if(ID.equals(BigInteger.valueOf(-1))){
                            notificationService.addNotification(receivedNotification);
                        }else {
                        	notiDate = sdf.parse(notificationService.getNotification(ID).getTimestamp());
                        	Date now = new Date();
                        	notiDate = new Date(notiDate.getTime() + account.getAggregationdate() * 3600000);
                        	if(now.after(notiDate)) {
                        		System.out.println("Adding notification to database. Aggregation is outdated.");
                        		notificationService.addNotification(receivedNotification);
                        	} else {
                        		System.out.println("Aggregation is up to date. Doing nothing.");
                        	}
                        	
                        }
                	} catch(Exception e) {
                		System.out.println("Problem occured while adding notification with agregation method: FIRST");
                		System.out.println("Exception:\n" + e.getMessage());
                	}

                    break;
            }
        }
        
        private AggregationMethod getAggregationType(int type) {
        	if(type == 0) {
        		return AggregationMethod.None;
        	} else if(type == 1) {
        		return AggregationMethod.First;
        	} else if(type == 2) {
        		return AggregationMethod.Last;
        	} else if(type == 3) {
        		return AggregationMethod.Count;
        	}
        	return AggregationMethod.None;
        }
    }

}

