package br.com.mnix.mazinrpcaiser.common.exception;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public class ServerExecutionException extends Exception {
	private static final long serialVersionUID = 3094551854536591783L;

	protected ServerExecutionException() {
	}

	protected ServerExecutionException(String message) {
		super(message);
	}

	protected ServerExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerExecutionException(Throwable cause) {
		super(cause);
	}

	protected ServerExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
