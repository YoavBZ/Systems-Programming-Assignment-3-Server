package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.impl.data.SharedData;

import java.time.LocalDateTime;
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
		String regex = "(\\w+=\"[^\"]*\")|\"([^\"]*)\"|(\\S+)";
		Pattern pattern = Pattern.compile(regex);
		String[] args = pattern.split(msg);
		switch (args[0]) {
			case "REGISTER":
				try {
					sharedData.getLock().writeLock().lock();
					processRegistration(args);
				} catch (Exception e) {
					connections.send(connectionId, error("registration"));
				} finally {
					sharedData.getLock().writeLock().unlock();
				}
				break;
			case "LOGIN":
				try {
					sharedData.getLock().writeLock().lock();
					if (sharedData.getRegisteredUsers().get(args[1]).equals(args[2]) && !sharedData.getLoggedUsers().containsValue(args[1])) {
						sharedData.getLoggedUsers().put(connectionId, args[1]);
						connections.send(connectionId, ack("login"));
					} else
						throw new UnsupportedOperationException("LOGIN failed");
				} catch (Exception e) {
					connections.send(connectionId, error("login"));
				} finally {
					sharedData.getLock().writeLock().unlock();
				}
				break;
			case "SIGNOUT":
				try {
					sharedData.getLock().writeLock().lock();
					if (sharedData.getLoggedUsers().remove(connectionId) != null) {
						connections.send(connectionId, ack("signout"));
						shouldTerminate = true;
					} else
						throw new UnsupportedOperationException("SIGNOUT failed");
				} catch (Exception e) {
					connections.send(connectionId, error("signout"));
				} finally {
					sharedData.getLock().writeLock().unlock();
				}
				break;
			case "REQUEST":
				try {
					processRequest(args);
				} catch (Exception e) {
					connections.send(connectionId, error("request " + args[1]));
				} finally {
					sharedData.getLock().readLock().unlock();
					sharedData.getLock().writeLock().unlock();
				}
		}
		System.out.println("[" + LocalDateTime.now() + "]: " + msg);
	}

	protected abstract void processRequest(String[] args) throws Exception;

	protected abstract void processRegistration(String[] args) throws Exception;

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