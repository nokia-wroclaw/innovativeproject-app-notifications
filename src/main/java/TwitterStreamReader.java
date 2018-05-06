import org.json.JSONObject;
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

public final class TwitterStreamReader {

    private static ApplicationContext context = new ClassPathXmlApplicationContext("file:Beans.xml");
    private static AccountJDBCTemplate accountJDBCTemplate = (AccountJDBCTemplate) context.getBean("AccountJDBCTemplate");
    private static Connection connection;
    private static Channel channel;
    private final static String QUEUE_NAME = "DEMO_QUEUE";
    private static String queueString;
    private static JSONObject lastQueueJSON;
    private static List<Account> userList = accountJDBCTemplate.getAccountBySourceId(15);
    private static Map<Long, Integer> userIDs = new HashMap<>();
    private static Map<Long, String> userHandles = new HashMap<>();
    private static final UserStreamListener listener = new UserStreamListener() {

        @Override
        public void onStatus(Status status) {
            for (Map.Entry<Long, String> handle : userHandles.entrySet()) {
                if (status.getText().toLowerCase().contains("@"+handle.getValue().toLowerCase())) {
                    String topic = status.getUser().getScreenName() + " has mentioned you in a tweet!";
                    System.out.println(topic);
                    try {
                        generateNote(status, topic, handle.getKey());
                    } catch (IOException | TimeoutException e) {
                        e.printStackTrace();
                    }
                }
            }
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
            System.out.println("Got stall warning:" + warning);
        }

        @Override
        public void onFriendList(long[] friendIds) {

        }

        @Override
        public void onFavorite(User source, User target, Status favoritedStatus) {
            String topic = "Tweet liked!";
            String message = source.getScreenName() + " has liked your tweet!";
            try {
                generateNote(topic, message, target.getId());
            } catch (IOException|TimeoutException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUnfavorite(User source, User target, Status unfavoritedStatus) {

        }

        @Override
        public void onFollow(User source, User followedUser) {
            String topic = "New follower!";
            String message = source.getScreenName() + " is now following " + followedUser.getScreenName() + "!";
            try {
                generateNote(topic, message, followedUser.getId());
            } catch (IOException|TimeoutException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUnfollow(User source, User followedUser) {

        }

        @Override
        public void onDirectMessage(DirectMessage directMessage) {
            String topic = "New direct message!";
            String message = directMessage.getSenderScreenName() + " has sent a message to " + directMessage.getRecipientScreenName() + "!";
            try {
                generateNote(topic, message, directMessage.getRecipientId());
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
            String topic = "Retweeted status!";
            try {
                generateNote(retweetedStatus, topic, target.getId());
            } catch (IOException|TimeoutException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFavoritedRetweet(User source, User target, Status favoritedRetweet) {

        }

        @Override
        public void onQuotedTweet(User source, User target, Status quotingTweet) {
            String topic = source.getName() + " has quoted your status!";
            try {
                generateNote(quotingTweet, topic, target.getId());
            } catch (IOException|TimeoutException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onException(Exception ex) {
            ex.printStackTrace();
            System.out.println("onException:" + ex.getMessage());
        }
    };


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

    private static void generateNote(Status status, String topic, Long twitterID) throws IOException, TimeoutException {
        org.json.JSONObject notificationJSON;
        notificationJSON = new org.json.JSONObject();
        notificationJSON.put("topic", topic);
        notificationJSON.put("message", status.getText());
        notificationJSON.put("sourceID", 15);
        notificationJSON.put("time", getCurrentTimeStamp());
        notificationJSON.put("priority", 0);
        notificationJSON.put("userID", userIDs.get(twitterID));

        queueString = notificationJSON.toString();
        if(doubleCheck(lastQueueJSON, notificationJSON)) {
            connectToQueue();
            sendJSON();
            closeConnection();
            lastQueueJSON = notificationJSON;
        }
    }

    private static void generateNote(String topic, String message, Long twitterID) throws IOException, TimeoutException {
        org.json.JSONObject notificationJSON;
        notificationJSON = new org.json.JSONObject();
        notificationJSON.put("topic", topic);
        notificationJSON.put("message", message);
        notificationJSON.put("sourceID", 15);
        notificationJSON.put("time", getCurrentTimeStamp());
        notificationJSON.put("priority", 0);
        notificationJSON.put("userID", userIDs.get(twitterID));

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


    private static void connectToQueue() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(5672);
        factory.setHost("");

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    }

    private static void closeConnection() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    private static void sendJSON() throws IOException {
        channel.basicPublish("", QUEUE_NAME, null, queueString.getBytes());
    }

    public static void main(String[] args) throws TwitterException {
        lastQueueJSON = new JSONObject();
        lastQueueJSON.put("topic", "topic");
        lastQueueJSON.put("message", "message");
        lastQueueJSON.put("sourceID", 0);
        lastQueueJSON.put("time", getCurrentTimeStamp());
        lastQueueJSON.put("priority", 0);
        lastQueueJSON.put("userID", 0);
        String accessToken;
        String accessTokenSecret;
        if(userList.isEmpty())
            System.out.println("User list is empty!");
        else {
            for (Account user : userList) {
                ConfigurationBuilder cb = new ConfigurationBuilder();
                accessToken = accountJDBCTemplate.getAccount(user.getAccountID()).getAccessToken();
                accessTokenSecret = accountJDBCTemplate.getAccount(user.getAccountID()).getAccessTokenSecret();

                cb.setDebugEnabled(true)
                        .setJSONStoreEnabled(true)
                        .setOAuthConsumerKey("")
                        .setOAuthConsumerSecret("")
                        .setOAuthAccessToken(accessToken)
                        .setOAuthAccessTokenSecret(accessTokenSecret);

                TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
                twitterStream.addListener(listener);
                userIDs.put(twitterStream.getId(), user.getUserID());
                userHandles.put(twitterStream.getId(), twitterStream.getScreenName());
                twitterStream.user();
            }
        }
    }
}
