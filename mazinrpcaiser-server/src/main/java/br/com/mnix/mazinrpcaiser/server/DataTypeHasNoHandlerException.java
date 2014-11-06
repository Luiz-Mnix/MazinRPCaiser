package br.com.mnix.mazinrpcaiser.server;

/**
 * Created by mnix05 on 11/6/14.
 *
 * @author mnix05
 */
public class DataTypeHasNoHandlerException extends Exception {
	private static final long serialVersionUID = -112128908156740124L;

	public DataTypeHasNoHandlerException() {
	}

	public DataTypeHasNoHandlerException(String message) {
		super(message);
	}

	public DataTypeHasNoHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataTypeHasNoHandlerException(Throwable cause) {
		super(cause);
	}

	public DataTypeHasNoHandlerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
