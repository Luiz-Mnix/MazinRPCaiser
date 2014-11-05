package br.com.mnix.mazinrpcaiser.server;

/**
 * Created by mnix05 on 11/5/14.
 *
 * @author mnix05
 */
public class ActionHandlerNotFoundException extends Exception {
	private static final long serialVersionUID = -7742576416595863445L;

	public ActionHandlerNotFoundException() {
	}

	public ActionHandlerNotFoundException(String message) {
		super(message);
	}

	public ActionHandlerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ActionHandlerNotFoundException(Throwable cause) {
		super(cause);
	}

	public ActionHandlerNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
