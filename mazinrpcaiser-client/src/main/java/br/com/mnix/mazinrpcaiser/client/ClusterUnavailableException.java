package br.com.mnix.mazinrpcaiser.client;

/**
 * Created by mnix05 on 11/4/14.
 *
 * @author mnix05
 */
public class ClusterUnavailableException extends Exception {
	private static final long serialVersionUID = 8894660807633041369L;

	public ClusterUnavailableException() {
	}

	public ClusterUnavailableException(String message) {
		super(message);
	}

	public ClusterUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClusterUnavailableException(Throwable cause) {
		super(cause);
	}

	public ClusterUnavailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
