package bgu.spl181.net.impl.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {

	@SerializedName("username")
	private String userName;
	@SerializedName("type")
	private String type;
	@SerializedName("password")
	private String password;
	@SerializedName("country")
	private String country;
	@SerializedName("movies")
	private List<UserMovie> movies = null;
	@SerializedName("balance")
	private String balance;

	public User(String userName, String password, String country) {
		this.userName = userName;
		this.type = "normal";
		this.password = password;
		this.country = country;
		this.balance = "0";
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<UserMovie> getMovies() {
		return movies;
	}

	public int getBalance() {
		return Integer.valueOf(balance);
	}

	public void setBalance(int balance) {
		this.balance = Integer.toString(balance);
		SharedData.gson.toJson("users.json", Users.class);
	}

	public void addUserMovie(String id, String movieName) {
		movies.add(new UserMovie(id, movieName));
		SharedData.gson.toJson("users.json", Users.class);
	}

	public boolean isAdmin() {
		return "admin".equals(type);
	}

	public void incBalance(int amount) {
		balance = Integer.toString(Integer.valueOf(balance) + amount);
		SharedData.gson.toJson("users.json", Users.class);
	}

	public void decBalance(int amount) {
		balance = Integer.toString(Integer.valueOf(balance) - amount);
		SharedData.gson.toJson("users.json", Users.class);
	}

	/**
	 * @param movieName A name of a movie
	 * @return True if the movie appears in the user's movie list, or false otherwise
	 */
	public boolean isRented(String movieName) {
		for (UserMovie movie : movies) {
			if (movieName.equals(movie.name))
				return true;
		}
		return false;
	}

	public void rentMovie(Movie movie) {
		movies.add(new User.UserMovie(String.valueOf(movie.getId()), movie.getName()));
		SharedData.gson.toJson("users.json", User.class);
	}

	public void returnMovie(Movie movie) {
		for (UserMovie userMovie : movies) {
			if (userMovie.id.equals(movie.getId())) {
				movies.remove(userMovie);
				break;
			}
		}
		SharedData.gson.toJson("users.json", User.class);
	}

	private class UserMovie {
		@SerializedName("id")
		@Expose
		private String id;
		@SerializedName("name")
		@Expose
		private String name;

		public UserMovie(String id, String movieName) {
			this.id = id;
			this.name = movieName;
		}

		public int getId() {
			return Integer.valueOf(id);
		}

		public void setId(int id) {
			this.id = Integer.toString(id);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
