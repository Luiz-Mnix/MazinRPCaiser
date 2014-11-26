package br.com.mnix.mazinrpcaiser.common.exception;

/**
 * Created by mnix05 on 11/5/14.
 *
 * @author mnix05
 */
public class InterfaceHasNoDefaultImplementationException extends Exception {
	private static final long serialVersionUID = 6063082608993428571L;

	public InterfaceHasNoDefaultImplementationException() {
	}

	public InterfaceHasNoDefaultImplementationException(String message) {
		super(message);
	}

	public InterfaceHasNoDefaultImplementationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InterfaceHasNoDefaultImplementationException(Throwable cause) {
		super(cause);
	}

	public InterfaceHasNoDefaultImplementationException(String message, Throwable cause, boolean enableSuppression,
														boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
