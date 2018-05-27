package restcontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import Model.User;
import Model.Notification;
import Model.Account;
import Model.Subscription;
import Model.Token;
import Model.TwitterRequest;
import jdbctemplate.AccountJDBCTemplate;
import jdbctemplate.NotificationJDBCTemplate;
import jdbctemplate.SubscriptionJDBCTemplate;
import jdbctemplate.UserJDBCTemplate;
import collection.NotificationCollection;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

@RestController
@Component
@RequestMapping("/api/v1.0")
public class RestControll {

	ApplicationContext context;// = new ClassPathXmlApplicationContext("file:Beans.xml");
    UserJDBCTemplate userService;// = (UserJDBCTemplate)context.getBean("userJDBCTemplate");
	SubscriptionJDBCTemplate subscriptionService;
	NotificationJDBCTemplate notificationService;
	AccountJDBCTemplate accountService;
	List<TwitterRequest> twitterLogging;
	
	
	private static final Logger log = LoggerFactory.getLogger(RestControll.class);
    
    public RestControll() {
    	context = new ClassPathXmlApplicationContext("Beans.xml");
    	userService = (UserJDBCTemplate)context.getBean("UserJDBCTemplate");
    	subscriptionService = (SubscriptionJDBCTemplate)context.getBean("SubscriptionJDBCTemplate");
    	notificationService = (NotificationJDBCTemplate)context.getBean("NotificationJDBCTemplate");
    	accountService = (AccountJDBCTemplate) context.getBean("AccountJDBCTemplate");
    	System.out.println("Setting up all JDBC services ended sucessfully");
    	twitterLogging = new ArrayList<TwitterRequest>();
    	log.info("Twitter logging structure ready to run");
    }
    
    //-----------------------------------------------------------------------------------
    //----------------------------------GET SECTION--------------------------------------
    //-----------------------------------------------------------------------------------
    
	// -------------------Retrieve All Users---------------------------------------------
    @ResponseBody
    @RequestMapping(value = "/user/", method = RequestMethod.GET)
    public ResponseEntity<?> listAllUsers() {
    	List<User> users;
    	try {
    		users = userService.listUsers();
    	} catch(EmptyResultDataAccessException e) {
    		log.info("Returning users returned with false.");
    		return new ResponseEntity<CustomErrorType>(new CustomErrorType("no data"),HttpStatus.NO_CONTENT);
    	}
        log.info("Returning users returned with true.");
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
        
    }
 
    // -------------------Retrieve Single User By ID------------------------------------------
    
    @ResponseBody
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable("id") int id) {
        try {
        	User user = userService.getUser(id);
        	log.info("Returning user {} returned with true.", id);
        	return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch(EmptyResultDataAccessException e) {
            log.error("User with id {} not found.", id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(Exception e) {
        	log.error("Error while getting user with id {}. Exception:\n" + e, id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        }
    }
    
    // -------------------Retrieve Single User By Login------------------------------------------
    
    @ResponseBody
    @RequestMapping(value = "/user/login/{login}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserL(@PathVariable("login") String login) {
    	User user;
        try {
        	user = userService.getUserByLogin(login);
        	log.info("Returning user {} returned with true.", login);
        	return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch(EmptyResultDataAccessException e) {
            log.error("User with login: {} not found.", login);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(IllegalStateException e) {
            log.error("User with login: {} not found.", login);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(Exception e) {
        	log.error("Error while getting user with login {}. Exception:\n" + e, login);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        }
    }
    
    // -------------------Retrieve All Subscriptions---------------------------------------------
    /*@ResponseBody
    @RequestMapping(value = "/sub/", method = RequestMethod.GET)
    public ResponseEntity<?> listAllSubscriptions() {
    	List<Subscription> subs = subscriptionService.listSubscriptions();
        if (subs.isEmpty()) {
        	log.info("Returning subscriptions returned with false.");
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("no data"),HttpStatus.NO_CONTENT);
        }
        log.info("Returning subscriptions returned with true.");
        return new ResponseEntity<List<Subscription>>(subs, HttpStatus.OK);
    }*/
    
    // -------------------Retrieve All Accounts---------------------------------------------
    @ResponseBody
    @RequestMapping(value = "/accounts/", method = RequestMethod.GET)
    public ResponseEntity<?> listAllSubscriptions() {
    	List<Account> accs = accountService.getAllAccounts();
        if (accs.isEmpty()) {
        	log.info("Returning accountss returned with false.");
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("no data"),HttpStatus.NO_CONTENT);
        }
        log.info("Returning accounts returned with true.");
        return new ResponseEntity<List<Account>>(accs, HttpStatus.OK);
    }
 
    // -------------------Retrieve Subscriptions of user------------------------------------------
    @ResponseBody
    @RequestMapping(value = "/sub/", method = RequestMethod.GET)
    public ResponseEntity<?> getSubscriptionsOfUser1(@RequestParam String token) {
        List<Subscription> subs = null;
        int id = 0;
    	try {
    		id = userService.getUserByToken(token).getUserId();
    		if(userService.getUser(id).getToken().equals(token)) {
    			subs = subscriptionService.getSubscription(id);
    			log.info("Returning subscriptions of user {} returned with true.",id);
            	return new ResponseEntity<List<Subscription>>(subs, HttpStatus.OK);
    		} else {
    			log.error("Failure while geting subscriptions of user {}. Dismatch between user ID and user TOKEN.\nID: " + id +" ;TOKEN:" + token,id);
    			return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.OK);
    		}
        } catch(EmptyResultDataAccessException e) {
            log.error("No subscriptions of user {} .", id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("no data"), HttpStatus.NOT_FOUND);
        } catch(IllegalStateException e) {
            log.error("Failure while geting subscriptions of user {}. IllegalStateException.", id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(Exception e) {
        	log.error("Error while getting subscriptions of user {}. Exception:\n" + e, id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        }
    }
    
    // -------------------Retrieve All Notifications Of User---------------------------------------------
    @ResponseBody
    @RequestMapping(value = "/not/", method = RequestMethod.GET)
    public ResponseEntity<?> listAllUserNotifications(@RequestParam String token) {
    	List<Notification> nots = null;
    	int id = 0;
    	try {
    		id = userService.getUserByToken(token).getUserId();
    		if(userService.getUser(id).getToken().equals(token)) {
    			nots = notificationService.getUserNotification(id);
            	NotificationCollection nColl = new NotificationCollection();
            	nColl.createNotificationCollection(nots);
            	return new ResponseEntity<NotificationCollection>(nColl,HttpStatus.OK);
    		} else {
    			log.error("Failure while geting notifications of user {}. Dismatch between user ID and user TOKEN.\nID: " + id +" ;TOKEN:" + token,id);
    			return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.OK);
    		}
        } catch(EmptyResultDataAccessException e) {
            log.error("Error while getting notifications od user {}. Empty Result Exception.", id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(IllegalStateException e) {
            log.error("Error while getting notifications od user {}. IllegalStateException.", id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(Exception e) {
        	log.error("Error while getting accounts of user {}. Exception:\n" + e, id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        }
    }
 
    // -------------------Retrieve All Not Read Notifications Of User------------------------------------------
    @ResponseBody
    @RequestMapping(value = "/notf/", method = RequestMethod.GET)
    public ResponseEntity<?> getSubscriptionsOfUser(@RequestParam String token) {
        List<Notification> nots = null;
        int id = 0;
    	try {
    		id = userService.getUserByToken(token).getUserId();
    		if(userService.getUser(id).getToken().equals(token)) {
    			nots = notificationService.getNoReadNotification(id);
            	NotificationCollection nColl = new NotificationCollection();
             	nColl.createNotificationCollection(nots);
             	return new ResponseEntity<NotificationCollection>(nColl,HttpStatus.OK);
    		} else {
    			log.error("Failure while geting notifications of user {}. Dismatch between user ID and user TOKEN.\nID: " + id +" ;TOKEN:" + token,id);
    			return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.OK);
    		}
        } catch(EmptyResultDataAccessException e) {
            log.error("Error while getting notifications od user {}. Empty Result Exception.", id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(IllegalStateException e) {
            log.error("Error while getting notifications od user {}. IllegalStateException.", id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(Exception e) {
        	log.error("Error while getting accounts of user {}. Exception:\n" + e, id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        }
    }
    
    //-------------------Retrieve Count Not Read Notifications Of User------------------------------------------
    @ResponseBody
    @RequestMapping(value = "/notf/part/", method = RequestMethod.GET)
    public ResponseEntity<?> getCountNotificationFalseOfUser(@RequestParam String offset, @RequestParam String token) {
        List<Notification> nots = null;
        int id = 0;
    	try {
    		id = userService.getUserByToken(token).getUserId();
    		if(userService.getUser(id).getToken().equals(token)) {
    			int offsetI = Integer.valueOf(offset);
        		
            	nots = notificationService.getCountNoReadUserNotification(id, 10, offsetI);
            	NotificationCollection nColl = new NotificationCollection();
             	nColl.createNotificationCollection(nots);
             	log.info("Getting notifications of user");
             	return new ResponseEntity<NotificationCollection>(nColl,HttpStatus.OK);
    		} else {
    			log.error("Failure while geting notifications of user {}. Dismatch between user ID and user TOKEN.\nID: " + id +" ;TOKEN:" + token,id);
    			return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.OK);
    		}
        } catch(EmptyResultDataAccessException e) {
            log.error("Error while getting notifications od user {}. Empty Result Exception.", id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(IllegalStateException e) {
            log.error("Error while getting notifications od user {}. IllegalStateException.", id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(Exception e) {
        	log.error("Error while getting accounts of user {}. Exception:\n" + e, id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        }
    }
    
    //-------------------Retrieve Count Notifications Of User------------------------------------------
    @ResponseBody
    @RequestMapping(value = "/not/part/", method = RequestMethod.GET)
    public ResponseEntity<?> getCountNotificationOfUser(@RequestParam String offset,@RequestParam String token) {
        List<Notification> nots = null;
        int id = 0;
    	try {
    		id = userService.getUserByToken(token).getUserId();
    		if(userService.getUser(id).getToken().equals(token)) {
    			int offsetI = Integer.valueOf(offset);
        		
            	nots = notificationService.getCountUserNotification(id, 10, offsetI);
            	NotificationCollection nColl = new NotificationCollection();
             	nColl.createNotificationCollection(nots);
             	return new ResponseEntity<NotificationCollection>(nColl,HttpStatus.OK);
    		} else {
    			log.error("Failure while geting notifications of user {}. Dismatch between user ID and user TOKEN.\nID: " + id +" ;TOKEN:" + token,id);
    			return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.OK);
    		}
        } catch(EmptyResultDataAccessException e) {
            log.error("Error while getting notifications od user {}. Empty Result Exception.", id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(IllegalStateException e) {
            log.error("Error while getting notifications od user {}. IllegalStateException.", id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(Exception e) {
        	log.error("Error while getting notifications of user {}. Exception:\n" + e, id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        }
    }
    
    //-------------------Retrieve All Not Read Notifications Of User From Specific Source------------------------------------------
    @ResponseBody
    @RequestMapping(value = "/notf/{id}/{sourceid}", method = RequestMethod.GET)
    public ResponseEntity<?> getSubscriptionsOfUserOfSource(@PathVariable("id") int id, @PathVariable("sourceid") int sid) {
        /*List<Notification> nots = null;
    	try {
    		//NOT WRITTEN YET
        	 //nots = notificationService.getNoReadNotification(id);
        	return new ResponseEntity<List<Notification>>(nots, HttpStatus.OK);
        } catch(EmptyResultDataAccessException e) {
        	System.out.println("User not found");
            log.error("No subscriptions of user {} .", id);
            return new ResponseEntity(new CustomErrorType("Notifications of user with id " + id 
                    + " not found").getErrorMessage(), HttpStatus.NOT_FOUND);
        }*/
    	return new ResponseEntity<String>("Not ready yet",HttpStatus.OK);
    }
    
    // -------------------Retrieve Specific Account------------------------------------------
    @ResponseBody
    @RequestMapping(value = "/acc/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getAccount(@PathVariable("id") int id, @RequestParam String token) {
        Account acco = null;
    	try {
    		if(userService.getUser(accountService.getAccount(id).getUserID()).getToken().equals(token)) {
    			acco = accountService.getAccount(id);
            	log.info("Getting specified account ended with true.");
            	return new ResponseEntity<Account>(acco, HttpStatus.OK);
    		} else {
    			log.error("Error while getting account. Account do not belong to the user.");
    			return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.OK);
    		}
        } catch(EmptyResultDataAccessException e) {
            log.error("Error while getting account.", id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(IllegalStateException e) {
            log.error("Error while getting account. IllegalStateException.", id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(Exception e) {
        	log.error("Error while getting account. Exception:\n" + e);
        	return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        }
    }
    
    // -------------------Retrieve Accounts Of Specified User------------------------------------------
    @ResponseBody
    @RequestMapping(value = "/acc/user/", method = RequestMethod.GET)
    public ResponseEntity<?> getUserAccounts(@RequestParam String token) {
        List<Account> acco = null;
    	int id = userService.getUserByToken(token).getUserId();
        try {
        	acco = accountService.AccountUserList(id);
        	return new ResponseEntity<List<Account>>(acco, HttpStatus.OK);
        } catch(EmptyResultDataAccessException e) {
            log.error("Error while getting accounts of user {}. Empty result data access exception", id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(IllegalStateException e) {
            log.error("Error while getting accounts of user {}. IllegalStateException.", id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(Exception e) {
        	log.error("Error while getting accounts of user {}. Exception:\n" + e, id);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        }
    }
    
    
    
    
    
    //-----------------------------------------------------------------------------------
    //---------------------------------POST SECTION--------------------------------------
    //-----------------------------------------------------------------------------------
    
    //-------------------Retrieve Token from login and password------------------------------------------
    @ResponseBody
    @RequestMapping(value = "/login/", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody String body) {
    	String login = null;
    	String password = null;
    	try {
    		
    		JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		login = json.get("login").toString();
    		password = json.get("password").toString();
    		
    		String token = userService.getUserByLogin(login).getToken();
    		if(password.equals(userService.getUserByLogin(login).getPassword())) {
    			log.info("Getting token of user {} returned with true. Returned token: {}.", login, token);
    			password = null;
    			login = null;
    			return new ResponseEntity<Token>(new Token(token),HttpStatus.OK);
    		} else {
    			password = null;
    			login = null;
    			//preLogin = null;
    			token = null;
    			log.info("Getting token of user {} returned with false. Wrong password", login);
    			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    		}
        } catch(EmptyResultDataAccessException e) {
            log.error("Error while getting token of user {}. Empty Result Exception.", login);
            log.error("Request body:\n" + body);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(IllegalStateException e) {
            log.error("Error while getting token of user {}. IllegalStateException.", login);
            log.error("Request body:\n" + body);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(Exception e) {
        	log.error("Error while getting token of user {}. Java Exception.\n" + e, login);
        	log.error("Request body:\n" + body);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        }
    }
    
    //---------------------------------Add new user by parameters---------------------------------------
    
    @RequestMapping(value = "/register/", method = RequestMethod.POST)
    public ResponseEntity<?> addUser(HttpServletRequest request,@RequestParam String username, 
    		@RequestParam String password, @RequestParam String name, 
    		@RequestParam String surname) {
    	
    	User user = null;
    	try {
    		user = new User();
    		user.setName(name);
    		user.setSurname(surname);
    		user.setLogin(username);
    		user.setPassword(password);
    		String token = UUID.randomUUID().toString();
    		while(!(userService.getUserByToken(token)==null)) {
    			token = UUID.randomUUID().toString();
    		}
    		user.setToken(token);
    		userService.createUser(user);
    	} catch(Exception e) {
			log.error("Error while adding new user {} {}.",name,surname);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
    	}
    	log.info("Adding new user finished with true. New user: {} {}",name,surname);
    	return new ResponseEntity<CustomStatusResponse>(new CustomStatusResponse("confirmed"),HttpStatus.OK);
    }
    
    //---------------------------------Add new user by JSON---------------------------------------
    
    @RequestMapping(value = "/register2/", method = RequestMethod.POST)
    public ResponseEntity<?> addUser2(HttpServletRequest request, @RequestBody User user) {
    	
    	try {
    		String token = UUID.randomUUID().toString();
    		log.info("Created first token of user {} {}. Token: {}.",user.getName(),user.getSurname(),token);
    		boolean goodToken = true;
    		while(goodToken) {
    			try {
    				userService.getUserByToken(token);
    				token = UUID.randomUUID().toString();
    			} catch (Exception e) {
    				goodToken = false;
    			}
    		}
    		log.info("Created final token of user {} {}",user.getName(),user.getSurname());
    		user.setToken(token);
    		userService.createUser(user);
    		log.info("Adding new user finished with true. New user: {} {}, his token: {}.",user.getName(),user.getSurname(),token);
        	return new ResponseEntity<CustomStatusResponse>(new CustomStatusResponse("confirmed"),HttpStatus.OK);
    	} catch(Exception e) {
			log.error("Error while adding new user {} {}. Exception:\n" + e,user.getName(),user.getSurname());
			log.error("Body:\n" + user);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
    	}
    }
    
    //------------------------Mark notification as read-------------------------
    @ResponseBody
    @RequestMapping(value = "/setFlag/", method = RequestMethod.POST)
    public ResponseEntity<?> changeFlagOfNotification(@RequestBody String body) {
    	//log.info("Trying to set read flag of notification {} to true.",notfid);
    	log.info("Body: {}",body);
    	String token = null;
    	int notfID = 0;
    	try {
    		JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		token = json.get("token").toString();
    		notfID = Integer.valueOf(json.get("notfid").toString());
    		if(userService.getUser(notificationService.getNotification(notfID).getUserID()).getToken().equals(token)) {
    			notificationService.setRead(notfID);
    			log.info("Setting read flag of notification {} returned with true.",notfID);
    			return new ResponseEntity<CustomStatusResponse>(new CustomStatusResponse("confirmed"),HttpStatus.OK);
    		} else {
    			log.error("Setting read flag of notification {} returned with false. Wrong token.",notfID);
    			return new ResponseEntity<CustomErrorType>(new CustomErrorType("wrong token"),HttpStatus.OK);
    		}
    	} catch(Exception e) {
    		log.error("Setting read flag of notification {} returned with false. Exception:\n" + e,notfID);
			return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.FORBIDDEN);
    	}
    }
    
    //-----------------------------Delete notification-----------------------------
    @ResponseBody
    @RequestMapping(value = "/notf/remove/", method = RequestMethod.POST)
    public ResponseEntity<?> deleteNotification(@RequestBody String body) {
    	Notification notification = null;
    	String token = null;
    	int notfID = 0;
    	try {
    		JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		token = json.get("token").toString();
    		notfID = Integer.valueOf(json.get("notfid").toString());
    		if(userService.getUser(notificationService.getNotification(notfID).getUserID()).getToken().equals(token)) {
    			notificationService.deleteNotification(notfID);
    			log.info("Deleting notification {} returned with true.",notfID);
    			return new ResponseEntity<CustomStatusResponse>(new CustomStatusResponse("confirmed"),HttpStatus.OK);
    		} else {
    			log.error("Deleting notification {} returned with false. Wrong token.",notfID);
    			return new ResponseEntity<CustomErrorType>(new CustomErrorType("wrong token"),HttpStatus.OK);
    		}
    	} catch(Exception e) {
    		log.error("Deleting notification {} returned with false. Exception:\n" + e,notfID);
			return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.FORBIDDEN);
    	}
    }
    
    //------------------------Add account of the user---------------------------
    @ResponseBody
    @RequestMapping(value = "/new/twitter/", method = RequestMethod.POST)
    public ResponseEntity<?> addAccount(@RequestBody String body) {
    	//String 
    	String accessToken = null;
    	String accessTokenSecret = null;
    	String token = null;
    	Account acc = null;
    	int userID = 0;
    	try {
    		JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		token = json.get("token").toString();
    		accessToken = json.get("accessToke").toString();
    		accessTokenSecret = json.get("secretToken").toString();
    		userID = userService.getUserByToken(token).getUserId();
    		acc = new Account();
    		acc.setUserID(userID);
    		acc.setAccessToken(accessToken);
    		acc.setAccessTokenSecret(accessTokenSecret);
    		acc.setSourceID(15);
    		//DO POPRAWY
    		acc.setLogin("login");
    		acc.setPassword("password");
    		//
    		accountService.createAccount(acc);
    		log.info("Adding new account to the user {} returned with true.",userID);
    		return new ResponseEntity<CustomStatusResponse>(new CustomStatusResponse("confirmed"),HttpStatus.OK);
    	} catch(Exception e) {
    		log.error("Adding new account for the user {} returned with false. Exception:\n" + e,userID);
			return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.FORBIDDEN);
    	}
    }
    
    //------------------------Add account of the user using twitter library---------------------------
    @ResponseBody
    @RequestMapping(value = "/new/twitter/request/", method = RequestMethod.POST)
    public ResponseEntity<?> addAccountRequest(@RequestBody String body) {
    	String url = null;
    	String token = null;
    	int userID = 0;
    	try {
    		JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		token = json.get("token").toString();
    		
    		userID = userService.getUserByToken(token).getUserId();
    		log.info("New twitter logging request.");
    		
    		for(int  i = 0;i < twitterLogging.size();i++) {
    			if(twitterLogging.get(i).getUserID()==userID) {
    				log.info("User {} already has TwitterRequest object. Deleting past object.",userID);
    				twitterLogging.remove(i);
    			}
    		}
    		
    		TwitterRequest userRequest = new TwitterRequest(userID);
    		log.info("Created new TwitterRequest object for user {}.",userID);
    		twitterLogging.add(userRequest);
    		log.info("Size of user twitter requests: {}",twitterLogging.size());
    		
            url = userRequest.getReqToken().getAuthenticationURL();
            if(!(url==null)) {
            	log.info("Url for authorization has returned with true.");
            	parser = null;
            	json = null;
            	token = null;
            	return new ResponseEntity<CustomStatusResponse>(new CustomStatusResponse(url),HttpStatus.OK);
            } else {
            	log.info("Url for authorization has returned with false. Unable to get Url.");
            	return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.OK);
            }
    	} catch(Exception e) {
    		log.error("Getting url for twitter authorization finished with false. Exception: \n" + e.getMessage());
    		return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.OK);
    	}
    	
    }
    
    //------------------------Add account of the user using twitter library confirming---------------------------
    @ResponseBody
    @RequestMapping(value = "/new/twitter/confirm/", method = RequestMethod.POST)
    public ResponseEntity<?> addAccountConfirm(@RequestBody String body) {
    	String pin = null;
    	String token = null;
    	int userID = 0;
    	String accessTokenU = null;
    	String accessTokenSecret = null;
    	Account account;
    	try {
    		
            AccessToken accessToken = null;
            
            JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		pin = json.get("pin").toString();
            token = json.get("token").toString();
            
            userID = userService.getUserByToken(token).getUserId();
            int toDelete = -1;
            
            TwitterRequest userRequest = null;
            for(int  i = 0;i < twitterLogging.size();i++) {
    			if(twitterLogging.get(i).getUserID()==userID) {
    				log.info("Found TwitterRequest Object of user {}.",userID);
    				userRequest = twitterLogging.get(i);
    				toDelete = i;
    			}
    		}
            
            account = new Account();
            
            if(toDelete==-1) {
            	log.info("No TwitterRequest object of user {}.",userID);
            	return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.OK);
            }
            
            accessToken = userRequest.getTwitter().getOAuthAccessToken(userRequest.getReqToken(), pin);
            accessTokenU = accessToken.getToken();
            accessTokenSecret = accessToken.getTokenSecret();
            
            log.info("User {} twitter accessToken: {}, and accessTokenSecret: {}.",accessTokenU,accessTokenSecret);
            
            //account.setAccountID(0);
            account.setLogin("unknownL");
            account.setPassword("unknownP");
            account.setAccessToken(accessTokenU);
            account.setAccessTokenSecret(accessTokenSecret);
            account.setSourceID(15);
            account.setUserID(userID);
            
            List<Account> userAcc = accountService.AccountUserList(userID);
            for(int i = 0 ;i <userAcc.size();i++) {
            	if(userAcc.get(i).getAccessToken().equals(accessTokenU)) {
            		log.info("This account is have been added by the user before.");
            		return new ResponseEntity<CustomStatusResponse>(new CustomStatusResponse("success"),HttpStatus.OK);
            	}
            }
            userAcc = null;
        	pin = null;
        	token = null;
        	accessToken = null;
        	
            accountService.createAccount(account);
            twitterLogging.remove(toDelete);
            log.info("Adding new Twitter account to the user {} returned with true.\nHis access token: {}, his access token secret {}.",userID,accessTokenU,accessTokenSecret);
            accessTokenU = null;
        	accessTokenSecret = null;
        	userID = 0;
            return new ResponseEntity<CustomStatusResponse>(new CustomStatusResponse("success"),HttpStatus.OK);
    	} catch(Exception e) {
    		log.error("Getting url for twitter authorization finished with false. Exception: \n" + e.getMessage());
    		return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.OK);
    	}
    }
    
    //------------------------Add user website to monitor---------------------------
    @ResponseBody
    @RequestMapping(value = "/new/website/", method = RequestMethod.POST)
    public ResponseEntity<?> addWebsite(@RequestBody String body) {
    	String url = null;
    	String token = null;
    	int userID = 0;
    	Account account;
    	try {
            
            JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
            token = json.get("token").toString();
            url = json.get("website").toString();
            
            userID = userService.getUserByToken(token).getUserId();
            
            account = new Account();
            
            if(url==null) {
            	log.info("Adding website to watch for user {} has returned with false.",userID);
            	return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.OK);
            }
            
            log.info("User {} website to monitor: {}.",url);
            
            account.setAccountID(0);
            account.setLogin("unknownL");
            account.setPassword("unknownP");
            account.setAccessToken(url);
            account.setAccessTokenSecret("noToken");
            account.setSourceID(10);
            account.setUserID(userID);
            
            List<Account> userAcc = accountService.AccountUserList(userID);
            for(int i = 0 ;i <userAcc.size();i++) {
            	if(userAcc.get(i).getAccessToken().equals(url)) {
            		log.info("This website has been added by the user before.");
            		return new ResponseEntity<CustomStatusResponse>(new CustomStatusResponse("success"),HttpStatus.OK);
            	}
            }
            userAcc = null;
        	token = null;
        	
            accountService.createAccount(account);
            log.info("Adding new website to monitor to the user {} returned with true.\nHis website: {}.",userID,url);
        	userID = 0;
        	url = null;
            return new ResponseEntity<CustomStatusResponse>(new CustomStatusResponse("success"),HttpStatus.OK);
    	} catch(Exception e) {
    		log.error("Getting url for twitter authorization finished with false. Exception: \n" + e.getMessage());
    		return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.OK);
    	}
    }
    
    //----------------------------------------------------------------
    //--------------------TEST SECTION--------------------------------
    //----------------------------------------------------------------
    
    @ResponseBody
    @RequestMapping(value = "/test/", method = RequestMethod.GET)
    public ResponseEntity<?> test(@RequestHeader HttpHeaders headers, @RequestBody User body) {
    	System.out.println(headers);
    	System.out.println(body.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
