package bgu.spl181.net.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MovieRentalProtocol extends UserServiceProtocol {

	private Map<Integer, User> users;
	private Map<String, Movie> movies;

	public MovieRentalProtocol(Map<Integer, User> users, Map<String, Movie> movies) {
		this.users = users;
		this.movies = movies;
	}

	@Override
	void handleRequest(List<String> args) {
		User user = users.get(connectionId);
		Movie movie;
		switch (args.get(1)) {
			case "balance":
				if (args.get(2).equals("info")) {
					connections.send(connectionId, "ACK balance " + user.getBalance());
					return;
				} else if (args.get(2).equals("add")) {
					user.incBalance(Integer.parseInt(args.get(3)));
					connections.send(connectionId, "ACK balance " + user.getBalance() + " added " + args.get(3));
					return;
				}
				break;
			case "info": {
				if (args.size() == 2) {
					connections.send(connectionId, "ACK info " + String.join(",", movies.keySet()));
					return;
				} else {
					movie = movies.get(args.get(2));
					connections.send(connectionId, "ACK info " + movie);
					return;
				}
			}
			case "rent":
				movie = movies.get(args.get(2));
				user.getRentedMovies().add(movie.getName());
				user.decBalance(movie.getPrice());
				movie.decCopiesLeft();
				connections.send(connectionId, "ACK rent " + movie.getName() + " success");
				connections.broadcast("movie " + movie.getName() + " " + movie.getCopiesLeft() + " " + movie.getPrice());
				return;
			case "return":
				movie = movies.get(args.get(2));
				if (user.getRentedMovies().remove(movie.getName())) {
					movie.incCopiesLeft();
					connections.send(connectionId, "ACK return " + movie.getName() + " success");
					connections.broadcast("movie " + movie.getName() + " " + movie.getCopiesLeft() + " " + movie.getPrice());
					return;
				}
				// Admin commands
			case "addmovie":
				if (user.isAdmin()) {
					movie = new Movie(args.get(2), Integer.valueOf(args.get(3)), Integer.valueOf(args.get(4)), new ArrayList<>());
					movies.put(movie.getName(), movie);
					connections.send(connectionId, "ACK addmovie " + movie.getName() + " success");
					connections.broadcast("movie " + movie.getName() + " " + movie.getCopiesLeft() + " " + movie.getPrice());
					return;
				}
			case "remmovie":
				if (user.isAdmin()) {
					String name = args.get(2);
					movies.remove(name);
					connections.send(connectionId, "ACK remmovie " + name + " success");
					connections.broadcast("movie " + name + " removed");
					return;
				}
			case "changeprice":
				if (user.isAdmin()) {
					movie = movies.get(args.get(2));
					movie.setPrice(Integer.parseInt(args.get(3)));
					connections.send(connectionId, "ACK changeprice " + movie.getName() + " success");
					connections.broadcast("movie " + movie.getName() + " " + movie.getCopiesLeft() + " " + movie.getPrice());
					return;
				}
		}
		connections.send(connectionId, "ERROR request " + args.get(2) + " failed");
	}
}
