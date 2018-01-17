package bgu.spl181.net.impl.BBreactor;

import bgu.spl181.net.impl.LineMessageEncoderDecoder;
import bgu.spl181.net.impl.MovieRentalProtocol;
import bgu.spl181.net.impl.data.MovieRentalSharedData;
import bgu.spl181.net.impl.data.movies.Movie;
import bgu.spl181.net.impl.data.users.MovieUser;
import bgu.spl181.net.srv.Server;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReactorMain {

	public static int main(String[] args) {
		try {
			ArrayList<MovieUser> users = new ArrayList<>();
			ArrayList<Movie> movies = new ArrayList<>();
			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			// Parsing the Users json
			JsonObject usersJson = parser.parse(new InputStreamReader(new FileInputStream("Database" + File.separator + "Users.json"))).getAsJsonObject();
			for (JsonElement element : usersJson.getAsJsonArray("users")) {
				users.add(gson.fromJson(element, MovieUser.class));
			}
			// Parsing the Movies json
			JsonObject moviesJson = parser.parse(new InputStreamReader(new FileInputStream("Database" + File.separator + "Movies.json"))).getAsJsonObject();
			for (JsonElement element : moviesJson.getAsJsonArray("movies")) {
				movies.add(gson.fromJson(element, Movie.class));
			}
			// Creating a new shared data based on the json files
			MovieRentalSharedData data = new MovieRentalSharedData(users, movies);
			// Launch server
			Server.reactor(
					7, Integer.parseInt(args[0]),
					() -> new MovieRentalProtocol(data),
					LineMessageEncoderDecoder::new).serve();
		} catch (Exception e) {
			return 1;
		}
		return 0;
	}
}
