package bgu.spl181.net.impl.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Movies {

	@SerializedName("movies")
	@Expose
	private List<Movie> movies = null;

	public List<Movie> getMovies() {
		return movies;
	}

	public void setMovies(List<Movie> movies) {
		this.movies = movies;
	}

	public Movie getMovie(String movieName) {
		for (Movie movie : movies) {
			if (movie.getName().equals(movieName))
				return movie;
		}
		return null;
	}

	public String getNames() {
		List<String> names = new ArrayList<>();
		for (Movie movie : movies) {
			names.add(movie.getName());
		}
		return "\"" + String.join("\" \"", names) + "\"";
	}

	public void addMovie(Movie movie) {
		movies.add(movie);
		SharedData.gson.toJson("movies.json", Movies.class);
	}

	public void removeMovie(Movie movieToRemove) {
		movies.remove(movieToRemove);
	}
}