import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public final class TwitterStreamReader {

    private static Connection connection;
    private static Channel channel;
    private final static String QUEUE_NAME = "DEMO_QUEUE";
    private static org.json.JSONObject queueJSON;
    private static String queueString;

    private static final UserStreamListener listener = new UserStreamListener() {

        @Override
        public void onStatus(Status status) {
            if (status.getText().contains("@Enfaude")) {
                String topic = "New mention of you made by " + status.getUser().getScreenName() + "!";
                try {
                    generateNote(status, topic);
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
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
//
        }

        @Override
        public void onScrubGeo(long userId, long upToStatusId) {
//
        }

        @Override
        public void onStallWarning(StallWarning warning) {
            System.out.println("Got stall warning:" + warning);
        }

        @Override
        public void onFriendList(long[] friendIds) {
           /* System.out.print("onFriendList");
            for (long friendId : friendIds) {
                System.out.print(" " + friendId);
            }
            System.out.println();*/
        }

        @Override
        public void onFavorite(User source, User target, Status favoritedStatus) {
            String topic = "Tweet liked!";
            String message = favoritedStatus.getUser().getScreenName() + " has liked your tweet!";
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
            String topic = "New follower!";
            String message = source.getScreenName() + " has followed " + followedUser.getScreenName() + "!";
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
//
        }

        @Override
        public void onUserDeletion(long deletedUser) {
//
        }

        @Override
        public void onUserSuspension(long suspendedUser) {
//
        }

        @Override
        public void onBlock(User source, User blockedUser) {

        }

        @Override
        public void onUnblock(User source, User unblockedUser) {

        }

        @Override
        public void onRetweetedRetweet(User source, User target, Status retweetedStatus) {
            System.out.println("onRetweetedRetweet source:@" + source.getScreenName()
                    + " target:@" + target.getScreenName()
                    + retweetedStatus.getUser().getScreenName()
                    + " - " + retweetedStatus.getText());

            String topic = "Retweeted status!";
            try {
                generateNote(retweetedStatus, topic);
            } catch (IOException|TimeoutException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFavoritedRetweet(User source, User target, Status favoritedRetweet) {
            /*System.out.println("onFavroitedRetweet source:@" + source.getScreenName()
                    + " target:@" + target.getScreenName()
                    + favoritedRetweet.getUser().getScreenName()
                    + " - " + favoritedRetweet.getText());*/
        }

        @Override
        public void onQuotedTweet(User source, User target, Status quotingTweet) {
            String topic = quotingTweet.getUser().getName() + " has quoted your status!";
            try {
                generateNote(quotingTweet, topic);
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


    private static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = Calendar.getInstance().getTime();

        return sdfDate.format(now);
    }

    private static void generateNote(Status status, String topic) throws IOException, TimeoutException {
        org.json.JSONObject notificationJSON;
        notificationJSON = new org.json.JSONObject();
        notificationJSON.put("topic", topic);
        notificationJSON.put("message", status.getText());
        notificationJSON.put("sourceID", 15);
        notificationJSON.put("time", getCurrentTimeStamp());
        notificationJSON.put("priority", 0);
        notificationJSON.put("userID", 4);

        String stringJSON = notificationJSON.toString();

        queueJSON = notificationJSON;
        queueString = stringJSON;
        connectToQueue();
        sendJSON();
        closeConnection();
    }

    private static void generateNote(String topic, String message) throws IOException, TimeoutException {
        org.json.JSONObject notificationJSON;
        notificationJSON = new org.json.JSONObject();
        notificationJSON.put("topic", topic);
        notificationJSON.put("message", message);
        notificationJSON.put("sourceID", 15);
        notificationJSON.put("time", getCurrentTimeStamp());
        notificationJSON.put("priority", 0);
        notificationJSON.put("userID", 4);

        String stringJSON = notificationJSON.toString();

        queueJSON = notificationJSON;
        queueString = stringJSON;
        connectToQueue();
        sendJSON();
        closeConnection();
    }



    private static void authenticate(TwitterStream twitterStream)throws TwitterException{
        Scanner input = new Scanner(System.in);

        // First, we ask Twitter for a request token.
        RequestToken reqToken = twitterStream.getOAuthRequestToken();
        System.out.println("\nRequest token: " + reqToken.getToken()
                + "\nRequest token secret: " + reqToken.getTokenSecret());
        AccessToken accessToken = null;
        while (accessToken == null) {
            System.out.print("\nOpen this URL in a browser: "
                    + "\n    " + reqToken.getAuthorizationURL() + "\n"
                    + "\nAuthorize the app, then enter the PIN here: ");
            String pin = input.nextLine();
            accessToken = twitterStream.getOAuthAccessToken(reqToken, pin);
        }
        System.out.println("\nAccess token: " + accessToken.getToken()
                + "\nAccess token secret: " + accessToken.getTokenSecret()
                + "\nSuccess!");
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
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setJSONStoreEnabled(true)
                .setOAuthConsumerKey("")
                .setOAuthConsumerSecret("")
                .setOAuthAccessToken("")
                .setOAuthAccessTokenSecret("");

        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

        if (!twitterStream.getAuthorization().isEnabled()) {
            authenticate(twitterStream);
        }

        twitterStream.addListener(listener);
        // user() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
        twitterStream.user();
    }
}