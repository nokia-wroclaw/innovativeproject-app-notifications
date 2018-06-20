package controller;


import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdbctemplate.AccountJDBCTemplate;
import jdbctemplate.NotificationJDBCTemplate;
import jdbctemplate.UserJDBCTemplate;
import model.Account;
import model.Token;

//Custom service - sourceID 1

@RestController
@Component
@RequestMapping("/api/v1.0")
public class RestControll {

	ApplicationContext context;
	
	UserJDBCTemplate userService;// = (UserJDBCTemplate)context.getBean("userJDBCTemplate");
	NotificationJDBCTemplate notificationService;
	AccountJDBCTemplate accountService;
	private static RabbitControll rabbit;
	private static AccountCheck accounts;
	private static final Logger log = LoggerFactory.getLogger(RestController.class);
	//To stop taking connections to database while sending notifications
	Thread checkAccountThread;
	
	public RestControll() {
		//log.info("before context");
		context = new ClassPathXmlApplicationContext("Beans.xml");
		//log.info("before services");
		userService = (UserJDBCTemplate)context.getBean("UserJDBCTemplate");
    	notificationService = (NotificationJDBCTemplate)context.getBean("NotificationJDBCTemplate");
    	accountService = (AccountJDBCTemplate) context.getBean("AccountJDBCTemplate");
    	rabbit = new RabbitControll();
    	
    	accounts = new AccountCheck(log, accountService);
    	String threadName = "Account listener";
    	checkAccountThread = new Thread(accounts, threadName);
    	checkAccountThread.start();
    	
    	log.info("All jdbc services setted up successfuly");
	}
	
	//--------------------------------------------------------------------------
	//-------------------------REST APPLICATION SECTION-------------------------
	//--------------------------------------------------------------------------
	
	@ResponseBody
	@RequestMapping(value = "/customservice/register/", method = RequestMethod.POST)
	public ResponseEntity<?> registerNewService(@RequestBody String body) {
		String token = null;
		String serviceToken = null;
		int userID = 0;
		Account customAccount;
		
		try {
			JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
			token = json.get("token").toString();
    		userID = userService.getUserByToken(token).getUserId();
    		
    		
    		
			serviceToken = UUID.randomUUID().toString();
    		log.info("Created first custom service token of user {}. Token: {}.",userID,serviceToken);
    		boolean goodToken = true;
    		while(goodToken) {
    			try {
    				accountService.getAccountByToken(1, serviceToken);
    				serviceToken = UUID.randomUUID().toString();
    			} catch (Exception e) {
    				goodToken = false;
    			}
    		}
    		log.info("Created final token of user {}. Token: {}.",userID, serviceToken);
    		
    		customAccount = new Account();
    		customAccount.setUserID(userID);
    		customAccount.setLogin("login");
    		customAccount.setPassword("password");
    		customAccount.setAccessToken(serviceToken);
    		customAccount.setAggregation(0);
    		customAccount.setAggregationdate(0);
    		customAccount.setAggregationkey("null");
    		customAccount.setAggregationtype(0);
    		customAccount.setSourceID(1);
    		
    		accountService.createAccount(customAccount);
    		log.info("New custom account added for user {}.", userID);
    		
    		//Add token to list in AccountCheck
    		accounts.getAccounts().put(token, userID);
    		
    		token = null;
    		userID = 0;
    		
    		return new ResponseEntity<Token>(new Token(serviceToken),HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception occured while registring new custom user external service. User id: {}. Exception:\n", userID);
			log.error(e.getMessage() + "\n" + e.getLocalizedMessage());
			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//------------Method which is passing custom notifications to the queue-----------------
	@ResponseBody
	@RequestMapping(value = "/customservice/notification/", method = RequestMethod.POST)
	public ResponseEntity<?> receiveNotification(@RequestBody String body) {
		String token = null;
		String topic = null;
		String message = null;
		String timestamp = null;
		int userID = 0;
		Account customAccount;
		try {
			JSONParser parser = new JSONParser();
    		JSONObject json = (JSONObject) parser.parse(body);
    		token = json.get("token").toString();
    		topic = json.get("topic").toString();
    		message = json.get("message").toString();
    		try {
    			timestamp = json.get("timestamp").toString();
    		} catch (Exception e) {
    			log.info("No timestamp in notification.");
    			timestamp = null;
    		}
    		
    		//customAccount = accountService.getAccountByToken(1, token);
    		userID = accounts.getAccounts().get(token);
    		
    		log.info("Custom notification from user {} custom account read successfully.", userID);
			
    		try {
    			if(timestamp == null) {
        			rabbit.generateNote(topic, message, userID);
        		} else {
        			rabbit.generateNote(topic, message, userID, timestamp);
        		}
    		} catch(Exception e) {
    			log.error("Error occured while sending custom notification to rabbit.");
    			log.error("Error: \n" + e.getMessage() + "\n" + e.getLocalizedMessage());
    			return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
    		}
    		
    		log.info("Custom notification from user {} custom account send successfully.", userID);
    		return new ResponseEntity<HttpStatus>(HttpStatus.OK);
    		
		} catch(Exception e) {
			log.error("Error occured while receiving notification from custom service. Exception: \n");
			log.error(e.getMessage() + "\n" + e.getLocalizedMessage());
			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//--------------------------------------------------------------------------
	//---------------------------RabbitMQ methods section-----------------------
	//--------------------------------------------------------------------------
	//Moved to different Object
}
