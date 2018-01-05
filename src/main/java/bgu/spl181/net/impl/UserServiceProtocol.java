package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import com.sun.deploy.util.ArgumentParsingUtil;

import java.time.LocalDateTime;
import java.util.List;

public abstract class UserServiceProtocol implements BidiMessagingProtocol<String> {

	protected int connectionId;
	protected Connections<String> connections;
	protected boolean shouldTerminate = false;

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
					// TODO: Process registration
					break;
				} catch (Exception e) {
					connections.send(connectionId, "ERROR registration failed");
				}
				connections.send(connectionId, "ACK registration succeeded");
				break;
			case "LOGIN":
				try {
					// TODO: Process login
					break;
				} catch (Exception e) {
					connections.send(connectionId, "ERROR login failed");
				}
				connections.send(connectionId, "ACK login succeeded");
				break;
			case "SIGNOUT":
				try {
					// TODO: Process signout
					break;
				} catch (Exception e) {
					connections.send(connectionId, "ERROR signout failed");
				}
				connections.send(connectionId, "ACK signout succeeded");
				break;
			case "REQUEST":
				try {
					handleRequest(args);
				} catch (Exception e) {
					connections.send(connectionId, "ERROR request " + args.get(1) + " failed");
				}
		}
		System.out.println("[" + LocalDateTime.now() + "]: " + msg);
	}

	abstract void handleRequest(List<String> arg);

	@Override
	public boolean shouldTerminate() {
		return shouldTerminate;
	}
}