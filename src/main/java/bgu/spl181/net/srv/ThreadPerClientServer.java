package bgu.spl181.net.srv;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.impl.ConnectionsImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public class ThreadPerClientServer<T> implements Server {

	private final int port;
	private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
	private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
	private ServerSocket sock;
	private ConnectionsImpl<T> connections = new ConnectionsImpl<>();

	public ThreadPerClientServer(
			int port,
			Supplier<BidiMessagingProtocol<T>> protocolFactory,
			Supplier<MessageEncoderDecoder<T>> encdecFactory) {
		this.port = port;
		this.protocolFactory = protocolFactory;
		this.encdecFactory = encdecFactory;
		this.sock = null;
	}

	@Override
	public void serve() {

		try (ServerSocket serverSock = new ServerSocket(port)) {
			System.out.println("Server started");

			this.sock = serverSock; //just to be able to close

			while (!Thread.currentThread().isInterrupted()) {

				Socket clientSock = serverSock.accept();

				BidiMessagingProtocol<T> protocol = protocolFactory.get();
				BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(
						clientSock,
						encdecFactory.get(),
						protocol);
				protocol.start(connections.addConnection(handler), connections);
				execute(handler);
			}
		} catch (IOException ignored) {
		}

		System.out.println("server closed!!!");
	}

	@Override
	public void close() throws IOException {
		if (sock != null)
			sock.close();
	}

	private void execute(BlockingConnectionHandler<T> handler) {
		new Thread(handler).start();
	}
}
