package bgu.spl181.net.impl;

import bgu.spl181.net.impl.data.users.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SharedData {

	private Map<Integer, String> loggedUsers = new HashMap<>();
	protected List<User> users = new ArrayList<>();
	private ReadWriteLock usersLock = new ReentrantReadWriteLock();
	private ReadWriteLock loggedUsersLock = new ReentrantReadWriteLock();

	public ReadWriteLock getUsersLock() {
		return usersLock;
	}

	public ReadWriteLock getLoggedUsersLock() {
		return loggedUsersLock;
	}


	public void addUser(User user) {
		users.add(user);
	}

	public Map<Integer, String> getLoggedUsers() {
		return loggedUsers;
	}

	public User getUser(String userName) {
		usersLock.readLock().lock();
		for (User user : users) {
			if (userName.equals(user.getUserName())) {
				usersLock.readLock().unlock();
				return user;
			}
		}
		usersLock.readLock().unlock();
		return null;
	}

	public boolean hasUser(String userName) {
		return getUser(userName) != null;
	}

	public String getPassword(String userName) {
		for (User user : users) {
			if (userName.equals(user.getUserName()))
				return user.getPassword();
		}
		return null;
	}

	public String getLoggedUserName(int connectionId) {
		loggedUsersLock.readLock().lock();
		String userName = loggedUsers.get(connectionId);
		loggedUsersLock.readLock().unlock();
		return userName;
	}

}
