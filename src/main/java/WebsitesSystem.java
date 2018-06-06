import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.Account;
import dao.AccountJDBCTemplate;

public class WebsitesSystem {

	private List<WebsiteStreamReader> listeners;
	private static List<Account> userList;
	private int listLength;
	private static ApplicationContext context;
    private static AccountJDBCTemplate accountService;
    private static final Logger log = LoggerFactory.getLogger(WebsitesSystem.class);
	
    //konstruktor
	public WebsitesSystem() {
		context = new ClassPathXmlApplicationContext("Beans.xml");
		accountService = (AccountJDBCTemplate) context.getBean("AccountJDBCTemplate");
		listeners = new ArrayList<WebsiteStreamReader>();
		userList = new ArrayList<Account>();
		listLength = userList.size();
		System.out.println("Creating new Website Reader microservice returnd with true.");
	}
	
	//metoda monitorujaca aktywnosc kont twitterowych
	public void monitoring() {
		System.out.println("Website System starts to monitor accounts.");
		boolean []toDelete;
		boolean []toAdd;
		List<Account> newAccounts;
		int delCounter;
		while(true) {
			listLength = userList.size();
			newAccounts = accountService.getAccountBySourceId(10);
			//Na poczatku usuwanie tych co juz nieaktywne
			toDelete = new boolean[listLength];
			toAdd = new boolean[newAccounts.size()];
			System.out.println("Checking Website accounts validity");
			System.out.println("New accounts list size " + newAccounts.size());
			System.out.println("Recent accounts list size " + userList.size());
			for(int i = 0;i < newAccounts.size();i++) toAdd[i] = true;
			
			for(int i = 0;i < userList.size();i++) {
				if(accountListContains(newAccounts, userList.get(i))) {
					toDelete[i] = false;
					if(i < newAccounts.size()) toAdd[i] = false;
					System.out.println("Account " + userList.get(i).getAccountID() + " ip updated.");
				} else {
					//usuniecie moze tu?
					System.out.println("Account " + userList.get(i).getAccountID() + " is outdated.");
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

					System.out.println("Deleted account " + userList.get(i).getAccountID());
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
					System.out.println("Adding account " + newAccounts.get(i).getAccountID() + ".");
					//System.out.println("Dodaje konto " + newAccounts.get(i).getAccountID());
					WebsiteStreamReader newReader = new WebsiteStreamReader(newAccounts.get(i).getAccessToken(),newAccounts.get(i).getUserID());
					listeners.add(newReader);
					String threadName = "WebsiteThread-" + newAccounts.get(i).getAccountID() + "-" + newAccounts.get(i).getUserID();
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
		WebsitesSystem app = new WebsitesSystem();
		app.monitoring();
	}
}
