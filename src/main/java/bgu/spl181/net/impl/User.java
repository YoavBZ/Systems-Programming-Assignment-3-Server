package bgu.spl181.net.impl;

import java.util.ArrayList;
import java.util.List;

public class User {

	private String userName;
	private String password;
	private int balance = 0;
	private String country;
	private boolean admin;
	private List<String> rentedMovies = new ArrayList<>();

	public User(String userName, String password, int balance, String country, boolean admin) {
		this.userName = userName;
		this.password = password;
		this.balance = balance;
		this.country = country;
		this.admin = admin;
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

	public String getCountry() {
		return country;
	}

	public boolean isAdmin() {
		return admin;
	}

	public List<String> getRentedMovies() {
		return rentedMovies;
	}
}
