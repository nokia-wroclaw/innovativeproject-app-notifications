package Model;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

public class TwitterRequest {

	private TwitterFactory tf;
    private Twitter twitter;
    private RequestToken reqToken;
    private int userID;
    
    public TwitterRequest(int userID) {
    	this.userID = userID;
    	
        // First, we ask Twitter for a request token.
        try {
        	tf = new TwitterFactory();
            twitter = tf.getInstance();
			reqToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e) {
			//TODO
		}
    }
    
    public RequestToken getReqToken() {
    	return this.reqToken;
    }
    
    public Twitter getTwitter() {
    	return this.twitter;
    }
    
    public int getUserID() {
    	return this.userID;
    }
    
    public void delete() {
    	this.tf = null;
    	this.twitter = null;
    	this.reqToken = null;
    	this.userID = 0;
    }
}
