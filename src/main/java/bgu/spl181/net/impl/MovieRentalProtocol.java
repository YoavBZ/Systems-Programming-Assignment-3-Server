package bgu.spl181.net.impl;

import bgu.spl181.net.impl.data.MovieRentalSharedData;
import bgu.spl181.net.impl.data.movies.Movie;
import bgu.spl181.net.impl.data.users.MovieUser;

import java.util.ArrayList;
import java.util.List;

public class MovieRentalProtocol extends UserServiceProtocol {

	public MovieRentalProtocol(MovieRentalSharedData sharedData) {
		super(sharedData);
	}

	@Override
	protected void processRegistration(List<String> args) {
		if (!sharedData.hasUser(args.get(1)) &&
				args.get(3).startsWith("country=") && args.size() == 4) {
			MovieUser movieUser = new MovieUser(args.get(1), args.get(2), args.get(3).substring(8));
			((MovieRentalSharedData) sharedData).addUser(movieUser);
			connections.send(connectionId, ack("registration"));
		} else
			throw new UnsupportedOperationException();
	}

	@Override
	protected void processRequest(List<String> args) {
		String userName = sharedData.getLoggedUserName(connectionId);
		MovieUser movieUser = (MovieUser) sharedData.getUser(userName);
		Movie movie;
		switch (args.get(1)) {
			case "balance":
				if (args.get(2).equals("info")) {
					try {
						sharedData.getUsersLock().readLock().lock();
						connections.send(connectionId, ack("balance " + movieUser.getBalance()));
						return;
					} finally {
						sharedData.getUsersLock().readLock().unlock();
					}
				} else if (args.get(2).equals("add")) {
					try {
						sharedData.getUsersLock().writeLock().lock();
						movieUser.incBalance(Integer.parseInt(args.get(3)));
						MovieRentalSharedData.saveJson("users", sharedData.users);
						connections.send(connectionId, ack("balance " + movieUser.getBalance() + " added " + args.get(3)));
						return;
					} finally {
						sharedData.getUsersLock().writeLock().unlock();
					}
				}
				break;
			case "info":
				try {
					((MovieRentalSharedData) sharedData).getMoviesLock().readLock().lock();
					if (args.size() == 2) {
						connections.send(connectionId, ack("info " + ((MovieRentalSharedData) sharedData).getMovieNames()));
						return;
					} else {
						movie = ((MovieRentalSharedData) sharedData).getMovie(args.get(2));
						connections.send(connectionId, ack("info " + movie.toString()));
						return;
					}
				} finally {
					((MovieRentalSharedData) sharedData).getMoviesLock().readLock().unlock();
				}
			case "rent":
				try {
					sharedData.getUsersLock().writeLock().lock();
					((MovieRentalSharedData) sharedData).getMoviesLock().writeLock().lock();
					movie = ((MovieRentalSharedData) sharedData).getMovie(args.get(2));
					if (movieUser.getBalance() >= movie.getPrice() && !movieUser.isRented(args.get(2)) && movie.getAvailableAmount() > 0 && !movie.getBannedCountries().contains(movieUser.getCountry())) {
						movieUser.rentMovie(movie);
						movieUser.decBalance(movie.getPrice());
						MovieRentalSharedData.saveJson("users", sharedData.users);
						movie.decAvailableAmount();
						MovieRentalSharedData.saveJson("movies", ((MovieRentalSharedData) sharedData).getMovies());
						connections.send(connectionId, ack("rent " + movie.getQuotedName() + " success"));
						broadcastToLoggedUsers("movie " + movie.getQuotedName() + " " + movie.getAvailableAmount() + " " + movie.getPrice());
						return;
					}
				} finally {
					sharedData.getUsersLock().writeLock().unlock();
					((MovieRentalSharedData) sharedData).getMoviesLock().writeLock().unlock();
				}
				break;
			case "return":
				try {
					sharedData.getUsersLock().writeLock().lock();
					((MovieRentalSharedData) sharedData).getMoviesLock().writeLock().lock();
					movie = ((MovieRentalSharedData) sharedData).getMovie(args.get(2));
					if (movieUser.isRented(args.get(2))) {
						movieUser.returnMovie(movie);
						MovieRentalSharedData.saveJson("users", sharedData.users);
						movie.incAvailableAmount();
						MovieRentalSharedData.saveJson("movies", ((MovieRentalSharedData) sharedData).getMovies());
						connections.send(connectionId, ack("return " + movie.getQuotedName() + " success"));
						broadcastToLoggedUsers("movie " + movie.getQuotedName() + " " + movie.getAvailableAmount() + " " + movie.getPrice());
						return;
					}
				} finally {
					sharedData.getUsersLock().writeLock().unlock();
					((MovieRentalSharedData) sharedData).getMoviesLock().writeLock().unlock();
				}
				break;
			// Admin commands
			case "addmovie":
				try {
					sharedData.getLoggedUsersLock().readLock().lock();
					((MovieRentalSharedData) sharedData).getMoviesLock().writeLock().lock();
					if (movieUser.isAdmin() && ((MovieRentalSharedData) sharedData).getMovie(args.get(2)) == null &&
							Integer.parseInt(args.get(3)) > 0 && Integer.parseInt(args.get(4)) > 0) {
						String movieId = String.valueOf(((MovieRentalSharedData) sharedData).getMaxMovieId().incrementAndGet());
						movie = new Movie(movieId, args.get(2), args.get(3), args.get(4), new ArrayList<>());
						((MovieRentalSharedData) sharedData).addMovie(movie);
						connections.send(connectionId, ack("addmovie " + movie.getQuotedName() + " success"));
						broadcastToLoggedUsers("movie " + movie.getName() + " " + movie.getAvailableAmount() + " " + movie.getPrice());
						return;
					}
				} finally {
					sharedData.getLoggedUsersLock().readLock().unlock();
					((MovieRentalSharedData) sharedData).getMoviesLock().writeLock().unlock();
				}
				break;
			case "remmovie":
				try {
					sharedData.getLoggedUsersLock().readLock().lock();
					((MovieRentalSharedData) sharedData).getMoviesLock().writeLock().lock();
					if (movieUser.isAdmin()) {
						movie = ((MovieRentalSharedData) sharedData).getMovie(args.get(2));
						if (movie.getAvailableAmount() == movie.getTotalAmount()) {
							((MovieRentalSharedData) sharedData).removeMovie(movie);
							connections.send(connectionId, ack("remmovie " + movie.getQuotedName() + " success"));
							broadcastToLoggedUsers("movie " + movie.getQuotedName() + " removed");
							return;
						}
					}
				} finally {
					sharedData.getLoggedUsersLock().readLock().unlock();
					((MovieRentalSharedData) sharedData).getMoviesLock().writeLock().unlock();
				}
				break;
			case "changeprice":
				try {
					sharedData.getLoggedUsersLock().readLock().lock();
					((MovieRentalSharedData) sharedData).getMoviesLock().writeLock().lock();
					if (movieUser.isAdmin() && Integer.parseInt(args.get(3)) > 0) {
						movie = ((MovieRentalSharedData) sharedData).getMovie(args.get(2));
						movie.setPrice(Integer.parseInt(args.get(3)));
						connections.send(connectionId, ack("changeprice " + movie.getName() + " success"));
						broadcastToLoggedUsers("movie " + movie.getName() + " " + movie.getAvailableAmount() + " " + movie.getPrice());
						return;
					}
				} finally {
					sharedData.getLoggedUsersLock().readLock().unlock();
					((MovieRentalSharedData) sharedData).getMoviesLock().writeLock().unlock();
				}
		}
		throw new UnsupportedOperationException();
	}
}
