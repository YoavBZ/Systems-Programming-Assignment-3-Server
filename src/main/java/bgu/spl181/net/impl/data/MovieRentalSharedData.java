package bgu.spl181.net.impl.data;

import java.util.concurrent.atomic.AtomicInteger;

public class MovieRentalSharedData extends SharedData {

	private Users users;
	private Movies movies;
	private AtomicInteger maxMovieId;

	public MovieRentalSharedData(Users users, Movies movies) {
		this.users = users;
		this.movies = movies;
		maxMovieId = new AtomicInteger();
		for (Movie movie : movies.getMovies()) {
			if (movie.getId() > maxMovieId.get())
				maxMovieId.set(movie.getId());
		}
	}

	public Users getUsers() {
		return users;
	}

	public Movies getMovies() {
		return movies;
	}

	public AtomicInteger getMaxMovieId() {
		return maxMovieId;
	}
}
