package bgu.spl181.net.srv;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;

import java.io.Closeable;
import java.util.function.Supplier;

public interface Server extends Closeable {

	/**
	 * The main loop of the server, Starts listening and handling new clients.
	 */
	void serve();

	/**
	 * This function returns a new instance of a thread per client pattern server
	 *
	 * @param port                  The port for the server socket
	 * @param protocolFactory       A factory that creates new MessagingProtocols
	 * @param encoderDecoderFactory A factory that creates new MessageEncoderDecoder
	 * @param <T>                   The Message Object for the protocol
	 * @return A new Thread per client server
	 */
	static <T> Server threadPerClient(
			int port,
			Supplier<BidiMessagingProtocol<T>> protocolFactory,
			Supplier<MessageEncoderDecoder<T>> encoderDecoderFactory) {
		return new ThreadPerClientServer<>(port, protocolFactory, encoderDecoderFactory);
	}

	/**
	 * This function returns a new instance of a reactor pattern server
	 *
	 * @param nThreads              Number of threads available for protocol processing
	 * @param port                  The port for the server socket
	 * @param protocolFactory       A factory that creates new MessagingProtocols
	 * @param encoderDecoderFactory A factory that creates new MessageEncoderDecoder
	 * @param <T>                   The Message Object for the protocol
	 * @return A new reactor server
	 */
	static <T> Server reactor(
			int nThreads,
			int port,
			Supplier<BidiMessagingProtocol<T>> protocolFactory,
			Supplier<MessageEncoderDecoder<T>> encoderDecoderFactory) {
		return new ReactorServer<>(nThreads, port, protocolFactory, encoderDecoderFactory);
	}
}
