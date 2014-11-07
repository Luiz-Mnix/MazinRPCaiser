package br.com.mnix.mazinrpcaiser.common.exception;

/**
 * Created by mnix05 on 11/5/14.
 *
 * @author mnix05
 */
public class InterfaceDoesNotExistException extends Exception {
	private static final long serialVersionUID = 1753355878496562851L;

	public InterfaceDoesNotExistException() {

	}

	public InterfaceDoesNotExistException(String message) {
		super(message);
	}

	public InterfaceDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public InterfaceDoesNotExistException(Throwable cause) {
		super(cause);
	}

	public InterfaceDoesNotExistException(String message, Throwable cause, boolean enableSuppression,
										  boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
