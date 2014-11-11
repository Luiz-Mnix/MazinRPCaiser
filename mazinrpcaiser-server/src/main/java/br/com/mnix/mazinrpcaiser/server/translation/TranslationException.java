package br.com.mnix.mazinrpcaiser.server.translation;

/**
 * Created by mnix05 on 11/10/14.
 *
 * @author mnix05
 */
public class TranslationException extends Exception {
	private static final long serialVersionUID = 3298242276847093322L;

	public TranslationException(String message) {
		super(message);
	}

	public TranslationException(String message, Throwable cause) {
		super(message, cause);
	}

	public TranslationException(Throwable cause) {
		super(cause);
	}
}
