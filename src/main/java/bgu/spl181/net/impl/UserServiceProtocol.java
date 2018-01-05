package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import com.sun.deploy.util.ArgumentParsingUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UserServiceProtocol implements BidiMessagingProtocol<String> {

	int connectionId;
	Connections<String> connections;
	Map<Integer, User> users = new HashMap<>();
	Map<Integer, User> loggedUsers = new HashMap<>();
	private boolean shouldTerminate = false;

	@Override
	public void start(int connectionId, Connections<String> connections) {
		this.connectionId = connectionId;
		this.connections = connections;
	}

	/**
	 * Processes a given message and uses {@link Connections} handles its response
	 *
	 * @param msg A given message
	 */
	@Override
	public void process(String msg) {
		List<String> args = ArgumentParsingUtil.parseCommandLine(msg);
		switch (args.get(0)) {
			case "REGISTER":
				try {
					handleRegister(args);
				} catch (Exception e) {
					connections.send(connectionId, error("registration"));
				}
				break;
			case "LOGIN":
				try {
					User user = users.get(connectionId);
					if (!loggedUsers.containsKey(connectionId) && args.get(1).equals(user.getUserName()) && args.get(2).equals(user.getPassword())) {
						loggedUsers.put(connectionId, users.get(connectionId));
						connections.send(connectionId, ack("login"));
					}
				} catch (Exception e) {
					connections.send(connectionId, error("login"));
				}
				break;
			case "SIGNOUT":
				try {
					if (loggedUsers.remove(connectionId) != null)
						connections.send(connectionId, ack("signout"));
				} catch (Exception e) {
					connections.send(connectionId, error("signout"));
				}
				break;
			case "REQUEST":
				try {
					handleRequest(args);
				} catch (Exception e) {
					connections.send(connectionId, error("request " + args.get(2)));
				}
		}
		System.out.println("[" + LocalDateTime.now() + "]: " + msg);
	}

	abstract void handleRequest(List<String> arg) throws Exception;

	protected void handleRegister(List<String> args) throws Exception {
		if (!users.containsKey(connectionId)) {
			handleRequest(args);
			connections.send(connectionId, ack("registration"));
			return;
		}
		throw new UnsupportedOperationException("Got an unsupported request.");
	}

	@Override
	public boolean shouldTerminate() {
		return shouldTerminate;
	}

	String ack(String response) {
		return "ACK " + response + " succeeded";
	}

	private String error(String response) {
		return "ERROR " + response + " failed";
	}
}