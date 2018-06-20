package collection;

import java.util.ArrayList;
import java.util.List;

import Model.Account;

public class AccountCollection {

	private List<Account> accounts;
	
	public AccountCollection() {
		accounts = new ArrayList<Account>();
	}
	
	public void createList(List<Account> list) {
		accounts.addAll(list);
	}
	
	public List<Account> getAccounts() {
		return accounts;
	}
}
