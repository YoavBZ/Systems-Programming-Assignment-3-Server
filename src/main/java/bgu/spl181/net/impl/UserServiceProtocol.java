package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.time.LocalDateTime;

public abstract class UserServiceProtocol<R extends ConnectionHandler<String>> implements BidiMessagingProtocol<String> {

	private int connectionId;
	private ConnectionsImpl<String, R> connections;
	private boolean shouldTerminate = false;

	@Override
	public void start(int connectionId, Connections<String> connections) {
		this.connectionId = connectionId;
		this.connections = (ConnectionsImpl<String, R>) connections;
	}

	/**
	 * Processes a given message and uses {@link Connections} handles its response
	 *
	 * @param msg A given message
	 */
	@Override
	public void process(String msg) {
		String[] args = msg.split(" ");
		switch (args[0]) {
			case "REGISTER":
				break;
			case "LOGIN":
				break;
			case "SIGNOUT":
				break;
			case "REQUEST":
				handleRequest(args[1]);
				break;
		}

		shouldTerminate = "bye".equals(msg);
		System.out.println("[" + LocalDateTime.now() + "]: " + msg);
		createEcho(msg);
	}

	private abstract void handleRequest(String arg);

	private String createEcho(String message) {
		String echoPart = message.substring(Math.max(message.length() - 2, 0), message.length());
		return message + " .. " + echoPart + " .. " + echoPart + " ..";
	}

	@Override
	public boolean shouldTerminate() {
		return shouldTerminate;
	}
}
