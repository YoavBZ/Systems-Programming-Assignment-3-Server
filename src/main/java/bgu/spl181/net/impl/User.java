package bgu.spl181.net.impl;

public class User {

	private String userName;
	private String password;
	private boolean admin;
	private int balance;
	private String[] data;

	public User(String userName, String password, boolean admin, int balance, String... data) {
		this.userName = userName;
		this.password = password;
		this.admin = admin;
		this.balance = balance;
		this.data = data;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public int getBalance() {
		return balance;
	}

	public void incBalance(int amount) {
		this.balance += amount;
	}

	public void decBalance(int amount) {
		this.balance -= amount;
	}

	public boolean isAdmin() {
		return admin;
	}

	public String[] getData() {
		return data;
	}
}
