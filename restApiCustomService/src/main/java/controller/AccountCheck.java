package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;

import jdbctemplate.AccountJDBCTemplate;

public class AccountCheck implements Runnable{

	private HashMap<String, Integer> customAccounts;
	private List<String> tokens;
	private AccountJDBCTemplate accountService;
	static Logger log;
	
	public AccountCheck(Logger log, AccountJDBCTemplate accountService) {
		this.customAccounts = new HashMap<String, Integer>();
		tokens = new ArrayList<String>();
		this.accountService = accountService;
		this.log = log;
		this.log.info("New hash map for controlling custom accounts created.");
	}
	
	@Override
	public void run() {
		while(true) {
			for(String token : customAccounts.keySet()) {
				try {
					accountService.getAccountByToken(1, token);
				} catch (Exception e) {
					log.info("Deleting custom account of user {}.", customAccounts.get(token));
					customAccounts.remove(token);
				}
				
			}
			try {
				Thread.sleep(900000);
			} catch(Exception e) {
				log.error("Thread occured problem!");
			}
		}
	}
	
	public HashMap<String, Integer> getAccounts() {
		return this.customAccounts;
	}
	
	public List<String> getTokens() {
		return this.tokens;
	}

}
