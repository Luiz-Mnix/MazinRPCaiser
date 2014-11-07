package br.com.mnix.mazinrpcaiser.server.service;

/**
 * Created by mnix05 on 11/6/14.
 *
 * @author mnix05
 */
public class RequestHasNoServiceException extends Exception {
	private static final long serialVersionUID = -112128908156740124L;

	public RequestHasNoServiceException() {
	}

	public RequestHasNoServiceException(String message) {
		super(message);
	}

	public RequestHasNoServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public RequestHasNoServiceException(Throwable cause) {
		super(cause);
	}

	public RequestHasNoServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
