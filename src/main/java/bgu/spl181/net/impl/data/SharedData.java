package bgu.spl181.net.impl.data;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SharedData {

	private Map<Integer, String> loggedUsers = new HashMap<>();
	private Map<String, String> registeredUsers = new HashMap<>();
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	public static Gson gson = new Gson();

	public Map<Integer, String> getLoggedUsers() {
		return loggedUsers;
	}

	public Map<String, String> getRegisteredUsers() {
		return registeredUsers;
	}

	public String getUserName(int connectionId) {
		return loggedUsers.get(connectionId);
	}

	public ReadWriteLock getLock() {
		return lock;
	}
}
