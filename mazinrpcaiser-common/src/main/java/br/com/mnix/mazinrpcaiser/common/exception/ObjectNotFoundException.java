package br.com.mnix.mazinrpcaiser.common.exception;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public class ObjectNotFoundException extends ServerExecutionException {
	private static final long serialVersionUID = -2572157098664952735L;

	public ObjectNotFoundException() {
	}

	public ObjectNotFoundException(String message) {
		super(message);
	}

	public ObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ObjectNotFoundException(Throwable cause) {
		super(cause);
	}

	public ObjectNotFoundException(String message, Throwable cause, boolean enableSuppression,
								   boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
