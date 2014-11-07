package br.com.mnix.mazinrpcaiser.client;

/**
 * Created by mnix05 on 11/4/14.
 *
 * @author mnix05
 */
public class DataGridUnavailableException extends Exception {
	private static final long serialVersionUID = 8894660807633041369L;

	public DataGridUnavailableException() {
	}

	public DataGridUnavailableException(String message) {
		super(message);
	}

	public DataGridUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataGridUnavailableException(Throwable cause) {
		super(cause);
	}

	public DataGridUnavailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
