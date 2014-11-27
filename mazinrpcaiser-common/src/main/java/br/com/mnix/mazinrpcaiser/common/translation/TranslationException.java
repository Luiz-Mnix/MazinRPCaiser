package br.com.mnix.mazinrpcaiser.common.translation;

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

// --Commented out by Inspection START (11/17/14, 4:02 PM):
//	public TranslationException(String message, Throwable cause) {
//		super(message, cause);
//	}
// --Commented out by Inspection STOP (11/17/14, 4:02 PM)

	public TranslationException(Throwable cause) {
		super(cause);
	}
	public TranslationException() {
		super();
	}
}
