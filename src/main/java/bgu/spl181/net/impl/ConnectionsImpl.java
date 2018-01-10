package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T> {

	private AtomicInteger idGenerator = new AtomicInteger();
	private HashMap<Integer, ConnectionHandler<T>> activeConnections = new HashMap<>();

	/**
	 * Adds a given handler the the {@link ConnectionsImpl#activeConnections} with a new unique id
	 *
	 * @param handler An already initiated {@link ConnectionHandler<T>}
	 */
	@Override
	public int addConnection(ConnectionHandler<T> handler) {
		int id = idGenerator.incrementAndGet();
		activeConnections.put(id, handler);
		return id;
	}

	/**
	 * Sends a message T to client represented by the given connId
	 *
	 * @param connectionId The connection id
	 * @param msg          The message to be sent
	 * @return true - if the message was sent to the client represented by connectionId that exists in {@link ConnectionsImpl#activeConnections})
	 * false - if the id doesn't exists in {@link ConnectionsImpl#activeConnections}
	 */
	@Override
	public boolean send(int connectionId, T msg) {
		ConnectionHandler<T> handler = activeConnections.get(connectionId);
		if (handler != null) {
			handler.send(msg);
			return true;
		}
		return false;
	}

	/**
	 * Sends a message T to all active clients.
	 * This includes clients that has not yet completed log-in by the MovieUser service text based protocol
	 *
	 * @param msg The message to be sent
	 */
	@Override
	public void broadcast(T msg) {
		for (ConnectionHandler<T> connectionHandler : activeConnections.values()) {
			connectionHandler.send(msg);
		}
	}

	/**
	 * Removes active client connId from map, after closing its {@link ConnectionHandler}
	 *
	 * @param connectionId The id of the connection to close
	 */
	@Override
	public void disconnect(int connectionId) throws IOException {
		ConnectionHandler<T> handler = activeConnections.remove(connectionId);
		if (handler != null)
			handler.close();
	}
}
