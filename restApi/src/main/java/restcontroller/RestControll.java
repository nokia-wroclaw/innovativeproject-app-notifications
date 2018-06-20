package restcontroller;

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

import Model.Account;
import Model.Notification;
import Model.Subscription;
import Model.Token;
import Model.TwitterRequest;
import Model.User;
import collection.AccountCollection;
import collection.NotificationCollection;
import collection.SourceNotificationCollection;
import collection.SourcesCollection;
import jdbctemplate.AccountJDBCTemplate;
import jdbctemplate.NotificationJDBCTemplate;
import jdbctemplate.SubscriptionJDBCTemplate;
import jdbctemplate.UserJDBCTemplate;
import twitter4j.auth.AccessToken;

@RestController
@Component
@RequestMapping("/api/v1.0")
public class RestControll {

	//EntityManagerFactory entityManagerFactory;// = Persistence.createEntityManagerFactory("nokiaDatabase");
    //EntityManager entityManager;// = entityManagerFactory.createEntityManager();
	ApplicationContext context;// = new ClassPathXmlApplicationContext("file:Beans.xml");
    //UserTemplate userService;// = (UserJDBCTemplate)context.getBean("userJDBCTemplate");
	//SubscriptionTemplate subscriptionService;
	//NotificationTemplate notificationService;
	//AccountTemplate accountService;
	UserJDBCTemplate userService;// = (UserJDBCTemplate)context.getBean("userJDBCTemplate");
	SubscriptionJDBCTemplate subscriptionService;
	NotificationJDBCTemplate notificationService;
	AccountJDBCTemplate accountService;
	List<TwitterRequest> twitterLogging;
	
	private static final Logger log = LoggerFactory.getLogger(RestControll.class);
    
    public RestControll() {
    	//entityManagerFactory = Persistence.createEntityManagerFactory("nokiaDatabase");
        //entityManager = entityManagerFactory.createEntityManager();
        
    	context = new ClassPathXmlApplicationContext("Beans.xml");
    	userService = (UserJDBCTemplate)context.getBean("UserJDBCTemplate");
    	subscriptionService = (SubscriptionJDBCTemplate)context.getBean("SubscriptionJDBCTemplate");
    	notificationService = (NotificationJDBCTemplate)context.getBean("NotificationJDBCTemplate");
    	accountService = (AccountJDBCTemplate) context.getBean("AccountJDBCTemplate");
    	
        //userService = new UserTemplate();
    	//subscriptionService = new SubscriptionTemplate();
    	//notificationService = new NotificationTemplate();
    	//accountService = new AccountTemplate();
    	
    	//userService.setEntityManagerFactory(entityManagerFactory);
    	//subscriptionService.setEntityManagerFactory(entityManagerFactory);
    	//notificationService.setEntityManagerFactory(entityManagerFactory);
    	//accountService.setEntityManagerFactory(entityManagerFactory);
    	
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
    
    //-------------------------Change user password------------------------------------
    @ResponseBody
    @RequestMapping(value = "/user/password/", method = RequestMethod.POST)
    public ResponseEntity<?> changeUserPassword(@RequestBody String body) {
    	String token = null;
    	String oldPassword = null;
    	String newPassword = null;
    	int userID = 0;
    	try {
    		JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		token = json.get("token").toString();
    		oldPassword = json.get("oldpassword").toString();
    		newPassword = json.get("newpassword").toString();
    		
    		userID = userService.getUserByToken(token).getUserId();
    		
    		if(userService.getUser(userID).getPassword().equals(oldPassword)) {
    			userService.changePassword(userID, newPassword);
        		
        		log.info("Changing password of user {} finished with true.", userID);
        		
        		return new ResponseEntity<CustomStatusResponse>(new CustomStatusResponse("success"),HttpStatus.OK);
    		} else {
    			log.info("Old password of user {} is wrong.", userID);
    			return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.CONFLICT);
    		}
    		
    	} catch (Exception e) {
    		log.error("Error while changing password of user {}. Exception:\n",userID);
    		log.error(e.getMessage() + "\n" + e.getLocalizedMessage());
    		return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.CONFLICT);
    	}
    }
    
    //-------------------------Change user mail------------------------------------
    @ResponseBody
    @RequestMapping(value = "/user/mail/", method = RequestMethod.POST)
    public ResponseEntity<?> changeUserMail(@RequestBody String body) {
    	String token = null;
    	String password = null;
    	String newMail = null;
    	int userID = 0;
    	try {
    		JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		token = json.get("token").toString();
    		password = json.get("password").toString();
    		newMail = json.get("mail").toString();
    		
    		userID = userService.getUserByToken(token).getUserId();
    		
    		if(userService.getUser(userID).getPassword().equals(password)) {
    			//userService.changeMail(userID, newMail);
        		
        		log.info("Changing mail of user {} finished with true.", userID);
        		
        		return new ResponseEntity<CustomStatusResponse>(new CustomStatusResponse("success"),HttpStatus.OK);
    		} else {
    			log.info("Password of user {} is wrong.", userID);
    			return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.CONFLICT);
    		}
    		
    	} catch (Exception e) {
    		log.error("Error while changing mail of user {}. Exception:\n",userID);
    		log.error(e.getMessage() + "\n" + e.getLocalizedMessage());
    		return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.CONFLICT);
    	}
    }
    
    //-------------------------Get notifiations by sources with count------------------
    @ResponseBody
    @RequestMapping(value = "/notifications/", method = RequestMethod.POST)
    public ResponseEntity<?> getNotificationsBySources(@RequestBody String body) {
    	String token = null;
    	int userID = 0;
    	List<Account> userAccounts = null;
    	try {
    		JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		token = json.get("token").toString();
    		
    		userID = userService.getUserByToken(token).getUserId();
    		
    		userAccounts = accountService.AccountUserList(userID);
    		
    		ArrayList<Integer> userSources = new ArrayList<Integer>();
    		
    		SourcesCollection userSourcesCollection = new SourcesCollection();
    		
    		for(int i = 0;i < userAccounts.size();i++) {
    			if(!(userSources.contains(userAccounts.get(i).getSourceID()))) {
    				userSources.add(userAccounts.get(i).getSourceID());
    				SourceNotificationCollection newSource = new SourceNotificationCollection();
    				newSource.setSourceID(userAccounts.get(i).getSourceID());
    				List<Notification> sourceNotifications;
    				try {
    					//Napisac ze od user id sourceID
    					sourceNotifications = notificationService.getCountUserNotificationFromSource(userID, newSource.getSourceID());
    					newSource.setCount(sourceNotifications.size());
    				} catch(Exception e) {
    					log.error("Error while getting notification from source {}, of user {}.", userAccounts.get(i).getSourceID(), userID);
    					log.equals("Exception:\n" + e.getMessage());
    					newSource.setCount(0);
    				}
    				
    				userSourcesCollection.getSources().add(newSource);
    			}
    		}
    		
    		log.info("Returning count of notification by sources of user {} finished with true.",userID);
    		return new ResponseEntity<SourcesCollection>(userSourcesCollection, HttpStatus.OK);
    	} catch(Exception e) {
    		log.error("Error while getting count of notifications by sources of user {}, his token {}", userID, token);
    		return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.CONFLICT);
    	}
    }
    
    //--------------------------------Get notification list of one source of user-----------------------
    @ResponseBody
    @RequestMapping(value = "/notifications/source/", method = RequestMethod.POST)
    public ResponseEntity<?> getUserNotificationsFromSource(@RequestBody String body) {
    	int sourceID = 0;
    	int userID = 0;
    	int offset = 0;
    	String token  = null;
    	NotificationCollection nColl;
    	try {
    		JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		token = json.get("token").toString();
    		
    		sourceID = Integer.valueOf(json.get("source").toString());
    		userID = userService.getUserByToken(token).getUserId();
    		offset = Integer.valueOf(json.get("offset").toString());
    		
    		nColl = new NotificationCollection();
    		nColl.createNotificationCollection(notificationService.getCountUserNotificationFromSource(userID,10, offset, sourceID));
    		
    		log.info("Getting notifications of user {} from source {} returned with true.",userID, sourceID);
    		return new ResponseEntity<NotificationCollection>(nColl, HttpStatus.OK);
    	} catch (Exception e) {
    		log.error("Error while getting notifiactions of user {} from specific source.",userID);
    		return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.CONFLICT);
    	}
    }
    
    //---------------------------------Add new user by JSON---------------------------------------
    
    @RequestMapping(value = "/register/", method = RequestMethod.POST)
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
    		try {
    			userService.getUserByLogin(user.getLogin());
    			log.error("This login alreadey exist!");
    			return new ResponseEntity<CustomErrorType> (new CustomErrorType("This login is not avalible!"), HttpStatus.BAD_REQUEST);
    		} catch(Exception e) {
    			log.info("Unique login. OK.");
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
    
    //---------------------Deleting a user with all his content-------------------
    @ResponseBody
    @RequestMapping(value = "/delete/user/", method = RequestMethod.POST)
    public ResponseEntity<?> deleteUser(@RequestBody String body) {
    	String token = null;
    	int userID = 0;
    	try {
    		JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		token = json.get("token").toString();
    		
    		userID = userService.getUserByToken(token).getUserId();
    		
    		notificationService.removeUserNotifications(userID);
    		log.info("Deleting all notifications of user {} finished with true.", userID);
    		accountService.removeUserAccount(userID);
    		log.info("Deleting all accounts of user {} finished with true.", userID);
    		userService.removeUser(userID);
    		log.info("Deleted account of user {}.", userID);
    		
    		return new ResponseEntity<HttpStatus> (HttpStatus.OK);
    	} catch (Exception e) {
    		log.info("Problem occured while deleting user {}. Exception:\n", userID);
    		log.error(e.getMessage() + "\n" + e.getCause() + "\n" + e.getLocalizedMessage());
    		return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
    	}
    }
    
    //------------------------Mark notification as read-------------------------
    @ResponseBody
    @RequestMapping(value = "/setFlag/", method = RequestMethod.POST)
    public ResponseEntity<?> changeFlagOfNotification(@RequestBody String body) {
    	//log.info("Trying to set read flag of notification {} to true.",notfid);
    	//log.info("Body: {}",body);
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
    		return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.BAD_REQUEST);
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
            
            log.info("User {} twitter accessToken: {}, and accessTokenSecret: {}.",userID,accessTokenU,accessTokenSecret);
            
            //account.setAccountID(0);
            account.setLogin("unknownL");
            account.setPassword("unknownP");
            account.setAccessToken(accessTokenU);
            account.setAccessTokenSecret(accessTokenSecret);
            account.setSourceID(15);
            account.setUserID(userID);
            account.setAggregation(0);
            account.setAggregationdate(0);
            
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
    		return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.BAD_REQUEST);
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
            account.setAggregation(0);
            account.setAggregationdate(0);
            
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
    		return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.BAD_REQUEST);
    	}
    }
    
    //---------------------Getting all accounts of specified user-------------------------
    @ResponseBody
    @RequestMapping(value = "/accounts/", method = RequestMethod.POST)
    public ResponseEntity<?> getUserAccounts(@RequestBody String body) {
    	String token = null;
    	AccountCollection accounts;
    	int userID = 0;
    	try {
    		
    		JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		token = json.get("token").toString();
    		
    		userID = userService.getUserByToken(token).getUserId();
    		accounts = new AccountCollection();
    		accounts.createList(accountService.AccountUserList(userID));
    		
    		log.info("Returning all accounts of user {} returned with true.",userID);
    		
    		return new ResponseEntity<AccountCollection>(accounts,HttpStatus.OK);
    		
        } catch(EmptyResultDataAccessException e) {
            log.error("Error while returning all accounts of user {}. Empty Result Exception.", userID);
            log.error("Request body:\n" + body);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(IllegalStateException e) {
            log.error("Error while returning all accounts of user {}. IllegalStateException.", userID);
            log.error("Request body:\n" + body);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(Exception e) {
        	log.error("Error while returning all accounts of user {}. Java Exception.\n" + e, userID);
        	log.error("Request body:\n" + body);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        }
    }
    
    //---------------------Remove account of specified user-------------------------
    @ResponseBody
    @RequestMapping(value = "/remove/account/", method = RequestMethod.POST)
    public ResponseEntity<?> removeUserAccount(@RequestBody String body) {
    	String token = null;
    	String sourceID = null;
    	String accessToken = null;
    	int userID = 0;
    	int source = 0;
    	try {
    		
    		JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		token = json.get("token").toString();
    		sourceID = json.get("source").toString();
    		accessToken = json.get("accesstoken").toString();
    		
    		source = Integer.valueOf(sourceID);
    		userID = userService.getUserByToken(token).getUserId();
    		
    		List<Account> accs = accountService.AccountUserList(userID);
    		for(int i = 0;i<accs.size();i++) {
    			if(accs.get(i).getSourceID() == source && accs.get(i).getAccessToken().equals(accessToken)) {
    				accountService.removeAccount(accs.get(i).getAccountID());
    				log.info("Recognized account {} of user {}. Removing account.",source,userID);
    	    		
    	    		return new ResponseEntity<CustomStatusResponse>(new CustomStatusResponse("success"),HttpStatus.OK);
    			}
    		}
    		
    		log.info("There is no account {} of user {}. Account does not exist.",source,userID);
    		token = null;
    		sourceID = null;
    		accessToken = null;
    		return new ResponseEntity<CustomStatusResponse>(new CustomStatusResponse("success"),HttpStatus.OK);
    		
        } catch(EmptyResultDataAccessException e) {
            log.error("Error while removing account of user {}. Empty Result Exception.", userID);
            log.error("Request body:\n" + body);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(IllegalStateException e) {
            log.error("Error while removing account of user {}. IllegalStateException.", userID);
            log.error("Request body:\n" + body);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        } catch(Exception e) {
        	log.error("Error while removing account of user {}. Java Exception.\n" + e, userID);
        	log.error("Request body:\n" + body);
            return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"), HttpStatus.NOT_FOUND);
        }
    }
    
    //--------------------Check status of the user account--------------------------
    @ResponseBody
    @RequestMapping(value = "/account/status/", method = RequestMethod.POST)
    public ResponseEntity<?> accountStatus(@RequestBody String body) {
    	int userID = 0;
    	int accountID = 0;
    	String token = null;
    	try {
    		JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		token = json.get("token").toString();
    		accountID = Integer.valueOf(json.get("account").toString());
    		
    		userID = userService.getUserByToken(token).getUserId();
    		//czy to ma sens?
    		log.info("Checking account {} status of user {} finished with true.",accountID,userID);
    		return new ResponseEntity<HttpStatus>(HttpStatus.OK);
    	} catch(Exception e) {
    		log.error("Error while checking account status of user {}. ", userID);
    		return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.OK);
    	}
    }
    
    //--------------------Change status of the user account----------------------
    @ResponseBody
    @RequestMapping(value = "/account/change/", method = RequestMethod.POST)
    public ResponseEntity<?> changeAccountStatus(@RequestBody String body) {
    	int userID = 0;
    	int accountID = 0;
    	String token = null;
    	try {
    		JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		token = json.get("token").toString();
    		accountID = Integer.valueOf(json.get("account").toString());
    		
    		userID = userService.getUserByToken(token).getUserId();
    		
    		//metoda zmieniajaca status i pyk
    		
    		log.info("Changing account {} status of user {} finished with true.",accountID,userID);
    		return new ResponseEntity<HttpStatus>(HttpStatus.OK);
    	} catch(Exception e) {
    		log.error("Error while changing account status of user {}. ", userID);
    		return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.OK);
    	}
    }
    
    //--------------------Change status of the user account----------------------
    @ResponseBody
    @RequestMapping(value = "/account/aggregation/", method = RequestMethod.POST)
    public ResponseEntity<?> changeAccountAgregationType(@RequestBody String body) {
    	int userID = 0;
    	int accountID = 0;
    	int aggregation = 0;
    	int aggregationDate = 0;
    	int aggregationType = 0;
    	int aggregationBy = 0;
    	String aggregationKey = null;
    	String token = null;
    	try {
    		JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		token = json.get("token").toString();
    		accountID = Integer.valueOf(json.get("account").toString());
    		aggregation = Integer.valueOf(json.get("aggregation").toString());
    		aggregationDate = Integer.valueOf(json.get("aggregationdate").toString());
    		aggregationType = Integer.valueOf(json.get("aggregationtype").toString());
    		aggregationBy = Integer.valueOf(json.get("aggregationby").toString());
    		aggregationKey = json.get("aggregationkey").toString();
    		
    		switch(aggregationType) {
    		case 0://godzina
    			//do nothing
    			break;
    		case 1://dzien
    			aggregationDate = aggregationDate * 24;
    			break;
    		case 2://tydzien
    			aggregationDate = aggregationDate * 24 * 7;
    			break;
    		case 3://miesiac
    			aggregationDate = aggregationDate * 24 * 30;
    			break;
    		case 4://rok
    			aggregationDate = aggregationDate * 24 * 30 * 12;
    			break;
    		}
    			
    		
    		userID = userService.getUserByToken(token).getUserId();
    		
    		accountService.updateAggregation(accountID, aggregation);
    		accountService.updateAggregationDate(accountID, aggregationDate);
    		accountService.updateAggregationBy(accountID, aggregationBy);
    		if(aggregationKey.equals("null")) {
    			accountService.updateAggregationKeys(accountID, null);
    		} else {
    			accountService.updateAggregationKeys(accountID, aggregationKey);
    		}
    		
    		log.info("Changing account {} agregation of user {} finished with true. His new aggregation type - {}.",accountID,userID,aggregationDate);
    		return new ResponseEntity<HttpStatus>(HttpStatus.OK);
    	} catch(Exception e) {
    		log.error("Error while changing account agregation of user {}. ", userID);
    		return new ResponseEntity<CustomErrorType>(new CustomErrorType("failure"),HttpStatus.OK);
    	}
    }
    
    
    
    //----------------------------------------------------------------
    //--------------------TEST SECTION--------------------------------
    //----------------------------------------------------------------
    
}
