package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class UserServiceProtocol implements BidiMessagingProtocol<String> {

	int connectionId;
	Connections<String> connections;
	private boolean shouldTerminate = false;
	final SharedData sharedData;

	public UserServiceProtocol(SharedData sharedData) {
		this.sharedData = sharedData;
	}

	@Override
	public void start(int connectionId, Connections<String> connections) {
		this.connectionId = connectionId;
		this.connections = connections;
	}

	/**
	 * Processes a given message and uses {@link Connections} to process its response
	 *
	 * @param msg A given message
	 */
	@Override
	public void process(String msg) {
		String regex = "(?:\\w+=\"([^\"]*)\")|\"Up([^\"]*)\"|(\\S+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(msg);
		List<String> args = new ArrayList<>();
		while (matcher.find()) {
			args.add(matcher.group());
		}
		switch (args.get(0)) {
			case "REGISTER":
				try {
					sharedData.getUsersLock().writeLock().lock();
					processRegistration(args);
				} catch (Exception e) {
					connections.send(connectionId, error("registration"));
				} finally {
					sharedData.getUsersLock().writeLock().unlock();
				}
				break;
			case "LOGIN":
				try {
					sharedData.getLoggedUsersLock().writeLock().lock();
					if (sharedData.getPassword(args.get(1)).equals(args.get(2)) && !sharedData.getLoggedUsers().containsValue(args.get(1))) {
						sharedData.getLoggedUsers().put(connectionId, args.get(1));
						connections.send(connectionId, ack("login"));
					} else
						throw new UnsupportedOperationException("LOGIN failed");
				} catch (Exception e) {
					connections.send(connectionId, error("login"));
				} finally {
					sharedData.getLoggedUsersLock().writeLock().unlock();
				}
				break;
			case "SIGNOUT":
				try {
					sharedData.getLoggedUsersLock().writeLock().lock();
					if (sharedData.getLoggedUsers().remove(connectionId) != null) {
						connections.send(connectionId, ack("signout"));
						shouldTerminate = true;
					} else
						throw new UnsupportedOperationException("SIGNOUT failed");
				} catch (Exception e) {
					connections.send(connectionId, error("signout"));
				} finally {
					sharedData.getLoggedUsersLock().writeLock().unlock();
				}
				break;
			case "REQUEST":
				try {
					processRequest(args);
				} catch (Exception e) {
					connections.send(connectionId, error("request " + args.get(1)));
				}
		}
		System.out.println("[" + LocalDateTime.now() + "]: " + msg);
	}

	void broadcastToLoggedUsers(String msg) {
		for (Integer connectionId : sharedData.getLoggedUsers().keySet()) {
			connections.send(connectionId, msg);
		}
	}

	protected abstract void processRequest(List<String> args) throws Exception;

	protected abstract void processRegistration(List<String> args) throws Exception;

	@Override
	public boolean shouldTerminate() {
		return shouldTerminate;
	}

	String ack(String msg) {
		return "ACK " + msg + " succeeded";
	}

	private String error(String msg) {
		return "ERROR " + msg + " failed";
	}
}