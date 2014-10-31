package br.com.mnix.mazinrpcaiser.common.exception;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public class ContextNotFoundException extends ServerException {
	private static final long serialVersionUID = -2519855466296375792L;

	public ContextNotFoundException() {
	}

	public ContextNotFoundException(String message) {
		super(message);
	}

	public ContextNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContextNotFoundException(Throwable cause) {
		super(cause);
	}

	public ContextNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
