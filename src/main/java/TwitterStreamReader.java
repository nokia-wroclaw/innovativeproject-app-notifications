import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            System.out.println("onStatus @" + status.getUser().getScreenName() + " - " + status.getText());
            String topic = "New status by " + status.getUser().getScreenName() + "!";
            try {
                generateNote(status, topic);
            } catch (IOException|TimeoutException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
//            System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
        }

        @Override
        public void onDeletionNotice(long directMessageId, long userId) {
//            System.out.println("Got a direct message deletion notice id:" + directMessageId);
        }

        @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
//            System.out.println("Got a track limitation notice:" + numberOfLimitedStatuses);
        }

        @Override
        public void onScrubGeo(long userId, long upToStatusId) {
//            System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
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
            /*System.out.println("onFavorite source:@"
                    + source.getScreenName() + " target:@"
                    + target.getScreenName() + " @"
                    + favoritedStatus.getUser().getScreenName() + " - "
                    + favoritedStatus.getText()
                    + "   |Tweet's URL: https://twitter.com/statuses/" + favoritedStatus.getId());*/
            String message = favoritedStatus.getUser().getScreenName() + " has liked your tweet!";
            try {
                generateNote(topic, message);
            } catch (IOException|TimeoutException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
            /*System.out.println("onUnFavorite source:@"
                    + source.getScreenName() + " target:@"
                    + target.getScreenName() + " @"
                    + unfavoritedStatus.getUser().getScreenName()
                    + " - " + unfavoritedStatus.getText());*/
        }

        @Override
        public void onFollow(User source, User followedUser) {
            System.out.println("onFollow source:@"
                    + source.getScreenName() + " target:@"
                    + followedUser.getScreenName());
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
            /*System.out.println("onFollow source:@"
                    + source.getScreenName() + " target:@"
                    + followedUser.getScreenName());*/
        }

        @Override
        public void onDirectMessage(DirectMessage directMessage) {
            System.out.println("onDirectMessage text:"
                    + directMessage.getText());
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
            /*System.out.println("onUserListMemberAddition added member:@"
                    + addedMember.getScreenName()
                    + " listOwner:@" + listOwner.getScreenName()
                    + " list:" + list.getName());*/
        }

        @Override
        public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
            /*System.out.println("onUserListMemberDeleted deleted member:@"
                    + deletedMember.getScreenName()
                    + " listOwner:@" + listOwner.getScreenName()
                    + " list:" + list.getName());*/
        }

        @Override
        public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
            /*System.out.println("onUserListSubscribed subscriber:@"
                    + subscriber.getScreenName()
                    + " listOwner:@" + listOwner.getScreenName()
                    + " list:" + list.getName());*/
        }

        @Override
        public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
            /*System.out.println("onUserListUnsubscribed subscriber:@"
                    + subscriber.getScreenName()
                    + " listOwner:@" + listOwner.getScreenName()
                    + " list:" + list.getName());*/
        }

        @Override
        public void onUserListCreation(User listOwner, UserList list) {
            /*System.out.println("onUserListCreated  listOwner:@"
                    + listOwner.getScreenName()
                    + " list:" + list.getName());*/
        }

        @Override
        public void onUserListUpdate(User listOwner, UserList list) {
            /*System.out.println("onUserListUpdated  listOwner:@"
                    + listOwner.getScreenName()
                    + " list:" + list.getName());*/
        }

        @Override
        public void onUserListDeletion(User listOwner, UserList list) {
            /*System.out.println("onUserListDestroyed  listOwner:@"
                    + listOwner.getScreenName()
                    + " list:" + list.getName());*/
        }

        @Override
        public void onUserProfileUpdate(User updatedUser) {
//            System.out.println("onUserProfileUpdated user:@" + updatedUser.getScreenName());
        }

        @Override
        public void onUserDeletion(long deletedUser) {
//            System.out.println("onUserDeletion user:@" + deletedUser);
        }

        @Override
        public void onUserSuspension(long suspendedUser) {
//            System.out.println("onUserSuspension user:@" + suspendedUser);
        }

        @Override
        public void onBlock(User source, User blockedUser) {
            /*System.out.println("onBlock source:@" + source.getScreenName()
                    + " target:@" + blockedUser.getScreenName());*/
        }

        @Override
        public void onUnblock(User source, User unblockedUser) {
            /*System.out.println("onUnblock source:@" + source.getScreenName()
                    + " target:@" + unblockedUser.getScreenName());*/
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
            System.out.println("onFavroitedRetweet source:@" + source.getScreenName()
                    + " target:@" + target.getScreenName()
                    + favoritedRetweet.getUser().getScreenName()
                    + " - " + favoritedRetweet.getText());
        }

        @Override
        public void onQuotedTweet(User source, User target, Status quotingTweet) {
            System.out.println("onQuotedTweet" + source.getScreenName()
                    + " target:@" + target.getScreenName()
                    + quotingTweet.getUser().getScreenName()
                    + " - " + quotingTweet.getText());
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

        String stringJSON = notificationJSON.toString();
        //System.out.println(stringJSON);

        /*PrintWriter writer = new PrintWriter("./src/main/resources/messagetest.json", "UTF-8");
        writer.println(stringJSON);
        writer.close();*/

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

        String stringJSON = notificationJSON.toString();
        //System.out.println(stringJSON);

        /*PrintWriter writer = new PrintWriter("./src/main/resources/messagetest.json", "UTF-8");
        writer.println(stringJSON);
        writer.close();*/

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