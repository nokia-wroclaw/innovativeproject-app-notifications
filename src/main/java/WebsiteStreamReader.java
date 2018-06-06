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

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class WebsiteStreamReader implements Runnable{

	private String url; //accessToken
	//private String url2; //accessTokenSecret
	private int userID;
	//private static final Logger log = LoggerFactory.getLogger(WebsiteStreamReader.class);
	private static ApplicationContext context;
    private Connection connection;
    private Channel channel;
    private String QUEUE_NAME = "DEMO_QUEUE";
    private String queueString;
    private JSONObject lastQueueJSON;
    private String newContent;
    private String recentContent;
    private boolean living;
    
    public WebsiteStreamReader(String accessToken, int userID) {
    	System.out.println("Creating new instance of WebsiteStreamReader.");
    	this.url = accessToken;
    	this.userID = userID;
    	newContent = null;
    	recentContent = null;
    	living = true;
    	
    	context = new ClassPathXmlApplicationContext("Beans.xml");
    	
    	
    	System.out.println("Created new instance of WebsiteStreamReader.");
    }


    private static boolean doubleCheck(JSONObject lastQueueJSON, JSONObject notificationJSON){
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

    private void generateNote(String topic, String message) throws IOException, TimeoutException {
        org.json.JSONObject notificationJSON;
        notificationJSON = new org.json.JSONObject();
        notificationJSON.put("topic", topic);
        notificationJSON.put("message", message);
        notificationJSON.put("sourceID", 10);
        notificationJSON.put("time", getCurrentTimeStamp());
        notificationJSON.put("priority", 0);
        notificationJSON.put("userID", userID);

        queueString = notificationJSON.toString();
        if(doubleCheck(lastQueueJSON, notificationJSON)) {
            connectToQueue();
            sendJSON();
            closeConnection();
            lastQueueJSON = notificationJSON;
        }
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

	@Override
	public void run() {
		//System.out.println("Starting running user {} twitter reader thread.",userID);
		System.out.println("Starting running user "+userID+" website reader thread.");
		lastQueueJSON = new JSONObject();
        lastQueueJSON.put("topic", "topic");
        lastQueueJSON.put("message", "message");
        lastQueueJSON.put("sourceID", 0);
        lastQueueJSON.put("time", getCurrentTimeStamp());
        lastQueueJSON.put("priority", 0);
        lastQueueJSON.put("userID", 0);
        if(userID == 0)
            System.out.println("User is not set!");
        else {
        	try {
        		newContent = getNewContent();
        		recentContent = getNewContent();
        		if(newContent.equals(recentContent)) System.out.println("Two objects equal to each other");
        	} catch(Exception e) {
        		//log.error("Configurating Website Reader of user {} failed.",userID);
        		System.out.println("Configurating of Website listener for user " + userID + " failed.");
        		living = false;
        	}
        }
        System.out.println("Website reader thread of user "+userID+" started.");
        //System.out.println("Website reader thread of user {} started.",userID);
        while(living) {
        	System.out.println("Thread running " + Thread.currentThread().getName());
			try {
				System.out.println("Thread running " + Thread.currentThread().getName());
				newContent = getNewContent();
				if(!(newContent.equals(recentContent))) {
					System.out.println("Last content:");
					System.out.println(recentContent);
					System.out.println("New content:");
					System.out.println(newContent);
					TimeUnit.SECONDS.sleep(60);
					System.out.println("Change occured on website " + url);
					generateNote("Zmiana na stronie " + url,"Nastąpiła zmiana na obserwowanej przez Ciebie stronie:\n"+url+"\nSzybko sprawdco się zmieniło!");
					recentContent = null;
					recentContent = newContent;
					newContent = null;
				}
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				//log.error("Problem with thread in Website Stream Reader of user {}.\nThread Exception:\n"+e.getMessage(),userID);
				e.printStackTrace();
			} catch (IOException e) {
				//log.error("Problem with thread in Website Stream Reader of user {}.\nIO Exception:\n"+e.getMessage(),userID);
				e.printStackTrace();
			} catch (TimeoutException e) {
				//log.error("Problem with thread in Website Stream Reader of user {}.\nTime Exception:\n"+e.getMessage(),userID);
				e.printStackTrace();
			}
		}
        //System.out.println("Twitter reader thread of user "+userID+" finished.");
        //System.out.println("Twitter reader thread of user {} finished.",userID);
	}
	
	private String getNewContent() throws IOException{
        URL link = new URL(url);
        URLConnection yc = link.openConnection();
        String newString = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            newString = newString+inputLine;
        }
        return newString;
    }
	
	public void stop() {
		living = false;
	}
}
