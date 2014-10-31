package br.com.mnix.mazinrpcaiser.common.exception;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public abstract class ServerException extends Exception {
	private static final long serialVersionUID = 3094551854536591783L;

	protected ServerException() {
	}

	protected ServerException(String message) {
		super(message);
	}

	protected ServerException(String message, Throwable cause) {
		super(message, cause);
	}

	protected ServerException(Throwable cause) {
		super(cause);
	}

	protected ServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
