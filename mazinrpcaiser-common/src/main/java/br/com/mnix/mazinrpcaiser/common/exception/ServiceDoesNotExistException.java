package br.com.mnix.mazinrpcaiser.common.exception;

/**
 * Created by mnix05 on 11/5/14.
 *
 * @author mnix05
 */
public class ServiceDoesNotExistException extends Exception {
	private static final long serialVersionUID = 1753355878496562851L;

	public ServiceDoesNotExistException() {

	}

	public ServiceDoesNotExistException(String message) {
		super(message);
	}

	public ServiceDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceDoesNotExistException(Throwable cause) {
		super(cause);
	}

	public ServiceDoesNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
