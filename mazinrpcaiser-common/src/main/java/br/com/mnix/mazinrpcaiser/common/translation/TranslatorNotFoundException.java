package br.com.mnix.mazinrpcaiser.common.translation;

/**
 * Created by mnix05 on 11/20/14.
 *
 * @author mnix05
 */
public class TranslatorNotFoundException extends TranslationException {
	private static final long serialVersionUID = 5743991141891490608L;

	public TranslatorNotFoundException(Class<?> type) {
		super("Translator not found for type " + type.getCanonicalName());
	}
}
