package br.com.mnix.mazinrpcaiser.common.exception;

/**
 * Created by mnix05 on 11/5/14.
 *
 * @author mnix05
 */
public class ServiceHasNoDefaultImplementationException extends Exception {
	private static final long serialVersionUID = 6063082608993428571L;

	public ServiceHasNoDefaultImplementationException() {
	}

	public ServiceHasNoDefaultImplementationException(String message) {
		super(message);
	}

	public ServiceHasNoDefaultImplementationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceHasNoDefaultImplementationException(Throwable cause) {
		super(cause);
	}

	public ServiceHasNoDefaultImplementationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
