package bgu.spl181.net.impl.data;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Users {

	@SerializedName("users")
	private List<User> users = new ArrayList<>();

	public List<User> getUsers() {
		return users;
	}

	public User getUser(String userName) {
		for (User user : users) {
			if (userName.equals(user.getUserName()))
				return user;
		}
		return null;
	}

	public boolean hasUser(String userName) {
		return getUser(userName) != null;
	}

	public void addUser(User user) {
		users.add(user);
		new Gson().toJson("users.json", Users.class);

	}

	public void updateUser(User user) {
		users.remove(user);
		users.add(user);
		new Gson().toJson("users.json", Users.class);
	}

	public void removeUser(User user) {
		users.remove(user);
		new Gson().toJson("users.json", Users.class);
	}
}
