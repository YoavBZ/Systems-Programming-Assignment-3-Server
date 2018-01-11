package bgu.spl181.net.impl.data;

import bgu.spl181.net.impl.SharedData;
import bgu.spl181.net.impl.data.movies.Movie;
import bgu.spl181.net.impl.data.users.MovieUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MovieRentalSharedData extends SharedData {

	private List<Movie> movies;
	private static Gson gson = new Gson();
	private AtomicInteger maxMovieId;
	private ReadWriteLock moviesLock = new ReentrantReadWriteLock();

	public MovieRentalSharedData(List<MovieUser> users, List<Movie> movies) {
		this.users = new ArrayList<>(users);
		this.movies = movies;
		maxMovieId = new AtomicInteger();
		for (Movie movie : movies) {
			if (movie.getId() > maxMovieId.get())
				maxMovieId.set(movie.getId());
		}
	}

	public AtomicInteger getMaxMovieId() {
		return maxMovieId;
	}

	// Users json
	public void addUser(MovieUser movieUser) {
		super.addUser(movieUser);
		saveJson("Users", users);
	}

	// Movies json
	public Movie getMovie(String movieName) {
		for (Movie movie : movies) {
			if (movie.getName().equals(movieName))
				return movie;
		}
		return null;
	}

	public String getMovieNames() {
		List<String> names = new ArrayList<>();
		for (Movie movie : movies) {
			names.add(movie.getName());
		}
		return "\"" + String.join("\" \"", names) + "\"";
	}

	public void addMovie(Movie movie) {
		movies.add(movie);
		saveJson("Movies", movies);
	}

	public void removeMovie(Movie movieToRemove) {
		movies.remove(movieToRemove);
		saveJson("Movies", movies);
	}

	public static void saveJson(String type, Object obj) {
		try (Writer writer = new FileWriter("Database" + File.separator + type + ".json")) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add(type.toLowerCase(), gson.toJsonTree(obj));
			gson.toJson(jsonObject, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ReadWriteLock getMoviesLock() {
		return moviesLock;
	}

	public List<Movie> getMovies() {
		return movies;
	}
}
