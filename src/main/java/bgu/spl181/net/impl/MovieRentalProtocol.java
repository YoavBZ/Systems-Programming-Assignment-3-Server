package bgu.spl181.net.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MovieRentalProtocol extends UserServiceProtocol {

	private Map<String, Movie> movies;
	private Map<User, List<String>> rentedMovies;

	public MovieRentalProtocol(Map<String, Movie> movies) {
		this.movies = movies;
	}

	/**
	 * This method handles the commands related to the Movie Rental Service
	 *
	 * @param args Original command arguments
	 */
	@Override
	void handleRequest(List<String> args) {
		if (args.get(0).equals("REGISTER")) {
			// Handling registration
			handleRegister(args);
		} else {
			// Handling request
			handleRequestCommand(args);
		}
	}

	@Override
	protected void handleRegister(List<String> args) {
		String[] data = null;
		if (args.size() >= 4) {
			data = Arrays.copyOfRange(((String[]) args.toArray()), 4, args.size());
		}
		User user = new User(args.get(1), args.get(2), false, 0, data);
		users.put(connectionId, user);
	}

	private void handleRequestCommand(List<String> args) {
		User user = users.get(connectionId);
		Movie movie;
		switch (args.get(1)) {
			// Handling request
			case "balance":
				if (args.get(2).equals("info")) {
					connections.send(connectionId, ack("balance " + user.getBalance()));
					return;
				} else if (args.get(2).equals("add")) {
					user.incBalance(Integer.parseInt(args.get(3)));
					connections.send(connectionId, ack("balance " + user.getBalance() + " added " + args.get(3)));
					return;
				}
				break;
			case "info": {
				if (args.size() == 2) {
					connections.send(connectionId, ack("info " + String.join(",", movies.keySet())));
					return;
				} else {
					movie = movies.get(args.get(2));
					connections.send(connectionId, ack("info " + movie));
					return;
				}
			}
			case "rent":
				movie = movies.get(args.get(2));
				rentedMovies.get(user).add(movie.getName());
				user.decBalance(movie.getPrice());
				movie.decCopiesLeft();
				connections.send(connectionId, ack("rent " + movie.getName() + " success"));
				connections.broadcast("movie " + movie.getName() + " " + movie.getCopiesLeft() + " " + movie.getPrice());
				return;
			case "return":
				movie = movies.get(args.get(2));
				if (rentedMovies.get(user).remove(movie.getName())) {
					movie.incCopiesLeft();
					connections.send(connectionId, ack("return " + movie.getName() + " success"));
					connections.broadcast("movie " + movie.getName() + " " + movie.getCopiesLeft() + " " + movie.getPrice());
					return;
				}
				// Admin commands
			case "addmovie":
				if (user.isAdmin()) {
					movie = new Movie(args.get(2), Integer.valueOf(args.get(3)), Integer.valueOf(args.get(4)), new ArrayList<>());
					movies.put(movie.getName(), movie);
					connections.send(connectionId, ack("addmovie " + movie.getName() + " success"));
					connections.broadcast("movie " + movie.getName() + " " + movie.getCopiesLeft() + " " + movie.getPrice());
					return;
				}
			case "remmovie":
				if (user.isAdmin()) {
					String name = args.get(2);
					movies.remove(name);
					connections.send(connectionId, ack("remmovie " + name + " success"));
					connections.broadcast("movie " + name + " removed");
					return;
				}
			case "changeprice":
				if (user.isAdmin()) {
					movie = movies.get(args.get(2));
					movie.setPrice(Integer.parseInt(args.get(3)));
					connections.send(connectionId, ack("changeprice " + movie.getName() + " success"));
					connections.broadcast("movie " + movie.getName() + " " + movie.getCopiesLeft() + " " + movie.getPrice());
					return;
				}
		}
		throw new UnsupportedOperationException("Got an unsupported request.");
	}
}
