import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeoutException;

import dao.*;

public final class TwitterStreamReader implements Runnable{

	private String accessToken;
    private String accessTokenSecret;
    private int userID = 0;
    private long twitterID;
    private String userName;
    private static final Logger log = LoggerFactory.getLogger(TwitterStreamReader.class);
    private static ApplicationContext context;
    private Connection connection;
    private Channel channel;
    private String QUEUE_NAME = "DEMO_QUEUE";
    private String queueString;
    private JSONObject lastQueueJSON;
    private boolean living;
    
    private UserStreamListener listener = new UserStreamListener() {
    
     
    	@Override
    	public void onStatus(Status status) {
    		if (status.getText().toLowerCase().contains("@"+userName)) {
                String topic = status.getUser().getScreenName() + " has mentioned you in a tweet!";
                try {
                    generateNote(status, topic);
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                    System.out.println(status.toString());
                }
            } else {
            	String topic = "New status by " + status.getUser().getName() + "!";
                try {
                    generateNote(status, topic);
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                    System.out.println(status.toString());
                }
            }
            log.info("Catched new status in user {} thread.",userID);
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

        }

        @Override
        public void onDeletionNotice(long directMessageId, long userId) {

        }

        @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

        }

        @Override
        public void onScrubGeo(long userId, long upToStatusId) {

        }

        @Override
        public void onStallWarning(StallWarning warning) {
        	log.info("Got stall warning:" + warning);
            System.out.println("Got stall warning:" + warning);
        }

        @Override
        public void onFriendList(long[] friendIds) {

        }

        @Override
        public void onFavorite(User source, User target, Status favoritedStatus) {
        	log.info("Catched onFavourite in user {} thread.",userID);
        	String topic = "Tweet liked!";
            String message = source.getScreenName() + " has liked your tweet!";
            try {
                generateNote(topic, message);
            } catch (IOException|TimeoutException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUnfavorite(User source, User target, Status unfavoritedStatus) {

        }

        @Override
        public void onFollow(User source, User followedUser) {
        	log.info("Catched new follower in user {} thread.",userID);
            String topic = "New follower!";
            String message = source.getScreenName() + " is now following " + followedUser.getScreenName() + "!";
            try {
                generateNote(topic, message);
            } catch (IOException|TimeoutException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUnfollow(User source, User followedUser) {

        }

        @Override
        public void onDirectMessage(DirectMessage directMessage) {
        	log.info("Catched directed message in user {} thread.",userID);
            String topic = "New direct message!";
            String message = directMessage.getSenderScreenName() + " has sent a message to " + directMessage.getRecipientScreenName() + "!";
            try {
                generateNote(topic, message);
            } catch (IOException|TimeoutException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {

        }

        @Override
        public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {

        }

        @Override
        public void onUserListSubscription(User subscriber, User listOwner, UserList list) {

        }

        @Override
        public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {

        }

        @Override
        public void onUserListCreation(User listOwner, UserList list) {

        }

        @Override
        public void onUserListUpdate(User listOwner, UserList list) {

        }

        @Override
        public void onUserListDeletion(User listOwner, UserList list) {

        }

        @Override
        public void onUserProfileUpdate(User updatedUser) {

        }

        @Override
        public void onUserDeletion(long deletedUser) {

        }

        @Override
        public void onUserSuspension(long suspendedUser) {

        }

        @Override
        public void onBlock(User source, User blockedUser) {

        }

        @Override
        public void onUnblock(User source, User unblockedUser) {

        }

        @Override
        public void onRetweetedRetweet(User source, User target, Status retweetedStatus) {
        	log.info("Catched retweeted retweet in user {} thread.",userID);
            String topic = "Retweeted status!";
            try {
                generateNote(retweetedStatus, topic);
            } catch (IOException|TimeoutException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFavoritedRetweet(User source, User target, Status favoritedRetweet) {

        }

        @Override
        public void onQuotedTweet(User source, User target, Status quotingTweet) {
        	log.info("Catched quoted tweet in user {} thread.",userID);
            String topic = source.getName() + " has quoted your status!";
            try {
                generateNote(quotingTweet, topic);
            } catch (IOException|TimeoutException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onException(Exception ex) {
        	log.info("Catched new exception! Exception:\n" + ex.getMessage());
            ex.printStackTrace();
            System.out.println("onException:" + ex.getMessage());
        }
    };
    
    public TwitterStreamReader(String accessToken, String accessTokenSecret, int userID) {
    	log.info("Creating new instance of TwitterStreamReader.");
    	this.accessToken = accessToken;
    	this.accessTokenSecret = accessTokenSecret;
    	this.userID = userID;
    	//this.userName = userName;
    	living = true;
    	
    	context = new ClassPathXmlApplicationContext("Beans.xml");
    	
    	
    	log.info("Created new instance of TwitterStreamReader.");
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

    private void generateNote(Status status, String topic) throws IOException, TimeoutException {
        org.json.JSONObject notificationJSON;
        notificationJSON = new org.json.JSONObject();
        notificationJSON.put("topic", topic);
        notificationJSON.put("message", status.getText());
        notificationJSON.put("sourceID", 15);
        notificationJSON.put("time", getCurrentTimeStamp());
        notificationJSON.put("priority", 0);
        notificationJSON.put("userID", userID);
        System.out.println("Przesylam status " + topic);
        queueString = notificationJSON.toString();
        if(doubleCheck(lastQueueJSON, notificationJSON)) {
            connectToQueue();
            sendJSON();
            closeConnection();
            lastQueueJSON = notificationJSON;
        }
    }

    private void generateNote(String topic, String message) throws IOException, TimeoutException {
        org.json.JSONObject notificationJSON;
        notificationJSON = new org.json.JSONObject();
        notificationJSON.put("topic", topic);
        notificationJSON.put("message", message);
        notificationJSON.put("sourceID", 15);
        notificationJSON.put("time", getCurrentTimeStamp());
        notificationJSON.put("priority", 0);
        notificationJSON.put("userID", userID);
        System.out.println("Przesylam status " + topic);
        queueString = notificationJSON.toString();
        if(doubleCheck(lastQueueJSON, notificationJSON)) {
            connectToQueue();
            sendJSON();
            closeConnection();
            lastQueueJSON = notificationJSON;
        }
    }


    private static void authenticate(TwitterStream twitterStream) throws TwitterException {
        Scanner input = new Scanner(System.in);
        RequestToken reqToken = twitterStream.getOAuthRequestToken();
        AccessToken accessToken = null;

        while (accessToken == null) {
            System.out.print("\nOpen this URL in a browser: "
                    + "\n    " + reqToken.getAuthorizationURL() + "\n"
                    + "\nAuthorize the app, then enter the PIN here: ");
            String pin = input.nextLine();
            accessToken = twitterStream.getOAuthAccessToken(reqToken, pin);
        }
        System.out.println("Success!");
    }


    private void connectToQueue() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(5672);
        factory.setHost("35.204.202.104");

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        //channel.Declare
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
		log.info("Starting running user {} twitter reader thread.",userID);
		System.out.println("Starting running user "+userID+" twitter reader thread.");
		lastQueueJSON = new JSONObject();
        lastQueueJSON.put("topic", "topic");
        lastQueueJSON.put("message", "message");
        lastQueueJSON.put("sourceID", 0);
        lastQueueJSON.put("time", getCurrentTimeStamp());
        lastQueueJSON.put("priority", 0);
        lastQueueJSON.put("userID", 0);
        String accessToken;
        String accessTokenSecret;
        if(userID == 0)
            System.out.println("User is not set!");
        else {
        	try {
        		ConfigurationBuilder cb = new ConfigurationBuilder();
                accessToken = this.accessToken;
                accessTokenSecret = this.accessTokenSecret;

                cb.setDebugEnabled(true)
                        .setJSONStoreEnabled(true)
                        .setOAuthConsumerKey("XcJtVXfdky45jDxXhRxj5dwZg")
                        .setOAuthConsumerSecret("4JEbKR6PzU8vF42WUMUHANUUIFs4tIHtjdRwSdldUvPxitqLfM")
                        .setOAuthAccessToken(accessToken)
                        .setOAuthAccessTokenSecret(accessTokenSecret);
                
                TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
                twitterStream.addListener(listener);
    			
                twitterID = twitterStream.getId();
    			userName = twitterStream.getScreenName();
    			
                twitterStream.user();
        	} catch(Exception e) {
        		log.error("Configurating Twitter Reader of user {} failed.",userID);
        		System.out.println("Configurating of Twitter listener for user " + userID + " failed.");
        		//living = false;
        		listener = null;
        	}
        }
        System.out.println("Twitter reader thread of user "+userID+" started.");
        log.info("Twitter reader thread of user {} started.",userID);
        while(living) {
        	System.out.println("Dziala watek " + Thread.currentThread().getName());
			try {
				System.out.println("Dziala watek " + Thread.currentThread().getName());
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
        System.out.println("Twitter reader thread of user "+userID+" finished.");
        log.info("Twitter reader thread of user {} finished.",userID);
	}
	
	public void stop() {
		living = false;
		listener = null;
		
	}
}
