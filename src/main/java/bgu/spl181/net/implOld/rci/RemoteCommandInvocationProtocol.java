package bgu.spl181.net.implOld.rci;

import java.io.Serializable;

public class RemoteCommandInvocationProtocol<T> implements MessagingProtocol<Serializable> {

	private T arg;

	public RemoteCommandInvocationProtocol(T arg) {
		this.arg = arg;
	}

	@Override
	public Serializable process(Serializable msg) {
		return ((Command) msg).execute(arg);
	}

	@Override
	public boolean shouldTerminate() {
		return false;
	}

}
