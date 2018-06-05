import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.Account;
import dao.AccountJDBCTemplate;

public class TwitterSystem {

	private List<TwitterStreamReader> listeners;
	private static List<Account> userList;
	private int listLength;
	private static ApplicationContext context;
    private static AccountJDBCTemplate accountService;
    private static final Logger log = LoggerFactory.getLogger(TwitterSystem.class);
	
    //konstruktor
	public TwitterSystem() {
		context = new ClassPathXmlApplicationContext("Beans.xml");
		accountService = (AccountJDBCTemplate) context.getBean("AccountJDBCTemplate");
		listeners = new ArrayList<TwitterStreamReader>();
		userList = new ArrayList<Account>();
		listLength = userList.size();
		log.info("Creating new Twitter Reader microservice returnd with true.");
	}
	
	//metoda monitorujaca aktywnosc kont twitterowych
	public void monitoring() {
		log.info("Twitter System starts to monitor accounts.");
		boolean []toDelete;
		boolean []toAdd;
		List<Account> newAccounts;
		int delCounter;
		while(true) {
			listLength = userList.size();
			newAccounts = accountService.getAccountBySourceId(15);
			//Na poczatku usuwanie tych co juz nieaktywne
			toDelete = new boolean[listLength];
			toAdd = new boolean[newAccounts.size()];
			log.info("Checking Twitter accounts validity");
			log.info("Lista nowych kont ma dlugosc " + newAccounts.size());
			log.info("Lista starych kont ma dlugosc " + userList.size());
			for(int i = 0;i < newAccounts.size();i++) toAdd[i] = true;
			
			for(int i = 0;i < listLength;i++) {
				if(accountListContains(newAccounts, userList.get(i))) {
					toDelete[i] = false;
					if(i < newAccounts.size()) toAdd[i] = false;
					log.info("Konto " + userList.get(i).getAccountID() + " aktualne.");
				} else {
					//usuniecie moze tu?
					log.info("Konto " + userList.get(i).getAccountID() + " nieaktualne.");
					toDelete[i] = true;
					//listeners.get(i).stop();
					//userList.remove(i);
					//listeners.remove(i);
				}
			}
			//usuniecie
			delCounter = userList.size();
			for(int i = 0;i < delCounter;i++) {
				if(toDelete[i]) {
					log.info("Deleting account {}.",userList.get(i).getAccountID());
					System.out.println("Usunelismy konto " + userList.get(i).getAccountID());
					listeners.get(i).stop();
					listeners.remove(i);
					userList.remove(i);
					i--;
					delCounter--;
				}
			}
			
			//sprawdzanie jakie trzeba dodac
			for(int i = 0;i < newAccounts.size();i++) {
				if(toAdd[i]) {
					log.info("Adding account {}.",newAccounts.get(i).getAccountID());
					System.out.println("Dodaje konto " + newAccounts.get(i).getAccountID());
					TwitterStreamReader newReader = new TwitterStreamReader(newAccounts.get(i).getAccessToken(),newAccounts.get(i).getAccessTokenSecret(),newAccounts.get(i).getUserID());
					listeners.add(newReader);
					String threadName = "TwitterThread-" + newAccounts.get(i).getAccountID() + "-" + newAccounts.get(i).getUserID();
					Thread newThread = new Thread(listeners.get((listeners.size()-1)),threadName);
					newThread.start();
					Account newAcc = new Account();
					newAcc.setAccessToken(newAccounts.get(i).getAccessToken());
					newAcc.setAccessTokenSecret(newAccounts.get(i).getAccessTokenSecret());
					newAcc.setLogin(newAccounts.get(i).getLogin());
					newAcc.setPassword(newAccounts.get(i).getPassword());
					newAcc.setUserID(newAccounts.get(i).getUserID());
					newAcc.setAccountID(newAccounts.get(i).getAccountID());
					userList.add(newAcc);	
					threadName = null;
				}
			}
			userList = newAccounts;
			
			newAccounts = null;
			toAdd = null;
			toDelete = null;
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	//meotda sprawdzajaca czy list zawiera konto o podanym ID
	public boolean accountListContains(List<Account> list, Account acc) {
		for(int i = 0;i < list.size();i++) {
			if(list.get(i).getAccountID() == acc.getAccountID()) {
				return true;
			}
		}
		return false;
	}
	
	//main
	public static void main(String[] args) {
		TwitterSystem app = new TwitterSystem();
		app.monitoring();
	}
}
