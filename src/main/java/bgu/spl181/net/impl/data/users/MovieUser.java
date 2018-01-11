package bgu.spl181.net.impl.data.users;

import bgu.spl181.net.impl.data.movies.Movie;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MovieUser extends User {

	@SerializedName("type")
	private String type;
	@SerializedName("country")
	private String country;
	@SerializedName("movies")
	private List<UserMovie> movies = new ArrayList<>();
	@SerializedName("balance")
	private String balance;

	public MovieUser(String userName, String password, String country) {
		super(userName, password);
		this.type = "normal";
		this.country = country;
		this.balance = "0";
	}

	public String getCountry() {
		return country;
	}

	public int getBalance() {
		return Integer.valueOf(balance);
	}

	public void addUserMovie(String id, String movieName) {
		movies.add(new UserMovie(id, movieName));
	}

	public boolean isAdmin() {
		return "admin".equals(type);
	}

	public void incBalance(int amount) {
		balance = Integer.toString(Integer.valueOf(balance) + amount);
	}

	public void decBalance(int amount) {
		balance = Integer.toString(Integer.valueOf(balance) - amount);
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
		movies.add(new MovieUser.UserMovie(String.valueOf(movie.getId()), movie.getName()));
	}

	public void returnMovie(Movie movie) {
		for (UserMovie userMovie : movies) {
			if (Integer.parseInt(userMovie.id) == movie.getId()) {
				movies.remove(userMovie);
				break;
			}
		}
	}

	private class UserMovie {
		@SerializedName("id")
		@Expose
		private String id;
		@SerializedName("name")
		@Expose
		private String name;

		UserMovie(String id, String movieName) {
			this.id = id;
			this.name = movieName;
		}
	}
}
