package bgu.spl181.net.impl;

import bgu.spl181.net.impl.data.Movie;
import bgu.spl181.net.impl.data.MovieRentalSharedData;
import bgu.spl181.net.impl.data.SharedData;
import bgu.spl181.net.impl.data.User;

import java.util.ArrayList;
import java.util.Map;

public class MovieRentalProtocol extends UserServiceProtocol {

	public MovieRentalProtocol(SharedData sharedData, Map<String, Movie> movies) {
		super(sharedData);
	}

	@Override
	protected void processRegistration(String[] args) {
		if (!((MovieRentalSharedData) sharedData).getUsers().hasUser(args[1]) &&
				args[3].startsWith("country=") && args.length == 4) {
			User user = new User(args[1], args[2], args[3].substring(8));
			((MovieRentalSharedData) sharedData).getUsers().addUser(user);
			connections.send(connectionId, ack("registration"));
		} else
			throw new UnsupportedOperationException();
	}

	@Override
	protected void processRequest(String[] args) {
		String userName = sharedData.getUserName(connectionId);
		User user = ((MovieRentalSharedData) sharedData).getUsers().getUser(userName);
		Movie movie;
		switch (args[1]) {
			// Handling request
			case "balance":
				if (args[2].equals("info")) {
					sharedData.getLock().readLock().lock();
					connections.send(connectionId, ack("balance " + user.getBalance()));
					return;
				} else if (args[2].equals("add")) {
					sharedData.getLock().writeLock().lock();
					user.incBalance(Integer.parseInt(args[3]));
					connections.send(connectionId, ack("balance " + user.getBalance() + " added " + args[3]));
					return;
				}
				break;
			case "info":
				sharedData.getLock().readLock().lock();
				if (args.length == 2) {
					connections.send(connectionId, ack("info " + ((MovieRentalSharedData) sharedData).getMovies().getNames()));
					return;
				} else {
					movie = ((MovieRentalSharedData) sharedData).getMovies().getMovie(args[2]);
					connections.send(connectionId, ack("info " + movie));
					return;
				}
			case "rent":
				sharedData.getLock().writeLock().lock();
				movie = ((MovieRentalSharedData) sharedData).getMovies().getMovie(args[2]);
				if (user.getBalance() >= movie.getPrice() && !user.isRented(args[2]) &&
						movie.getAvailableAmount() > 0 && !movie.getBannedCountries().contains(user.getCountry())) {
					user.rentMovie(movie);
					user.decBalance(movie.getPrice());
					movie.decAvailableAmount();
					connections.send(connectionId, ack("rent " + movie.getQuotedName() + " success"));
					connections.broadcast("movie " + movie.getQuotedName() + " " + movie.getAvailableAmount() + " " + movie.getPrice());
					return;
				}
				break;
			case "return":
				sharedData.getLock().writeLock().lock();
				movie = ((MovieRentalSharedData) sharedData).getMovies().getMovie(args[2]);
				if (user.isRented(args[2])) {
					user.returnMovie(movie);
					movie.incAvailableAmount();
					connections.send(connectionId, ack("return " + movie.getQuotedName() + " success"));
					connections.broadcast("movie " + movie.getQuotedName() + " " + movie.getAvailableAmount() + " " + movie.getPrice());
					return;
				}
				break;
			// Admin commands
			case "addmovie":
				sharedData.getLock().writeLock().lock();
				if (user.isAdmin() && ((MovieRentalSharedData) sharedData).getMovies().getMovie(args[2]) == null &&
						Integer.parseInt(args[3]) > 0 && Integer.parseInt(args[4]) > 0) {
					String movieId = String.valueOf(((MovieRentalSharedData) sharedData).getMaxMovieId().incrementAndGet());
					movie = new Movie(movieId, args[2], args[3], args[4], new ArrayList<>());
					((MovieRentalSharedData) sharedData).getMovies().addMovie(movie);
					connections.send(connectionId, ack("addmovie " + movie.getQuotedName() + " success"));
					connections.broadcast("movie " + movie.getName() + " " + movie.getAvailableAmount() + " " + movie.getPrice());
					return;
				}
				break;
			case "remmovie":
				sharedData.getLock().writeLock().lock();
				if (user.isAdmin()) {
					movie = ((MovieRentalSharedData) sharedData).getMovies().getMovie(args[2]);
					if (movie.getAvailableAmount() == movie.getTotalAmount()) {
						((MovieRentalSharedData) sharedData).getMovies().removeMovie(movie);
						connections.send(connectionId, ack("remmovie " + movie.getQuotedName() + " success"));
						connections.broadcast("movie " + movie.getQuotedName() + " removed");
						return;
					}
				}
				break;
			case "changeprice":
				sharedData.getLock().writeLock().lock();
				if (user.isAdmin() && Integer.parseInt(args[3]) > 0) {
					movie = ((MovieRentalSharedData) sharedData).getMovies().getMovie(args[2]);
					movie.setPrice(Integer.parseInt(args[3]));
					connections.send(connectionId, ack("changeprice " + movie.getName() + " success"));
					connections.broadcast("movie " + movie.getName() + " " + movie.getAvailableAmount() + " " + movie.getPrice());
					return;
				}
		}
		throw new UnsupportedOperationException();
	}
}
