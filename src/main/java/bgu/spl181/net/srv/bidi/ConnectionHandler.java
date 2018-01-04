package bgu.spl181.net.srv.bidi;

import java.io.Closeable;

/**
 * @author bennyl
 */
public interface ConnectionHandler<T> extends Closeable {

	void send(T msg);
}