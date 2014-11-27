package br.com.mnix.mazinrpcaiser.common.translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by mnix05 on 11/11/14.
 *
 * @author mnix05
 */
public interface ITranslator {
	@Nonnull Object translate(@Nonnull Object data, @Nullable ITranslator fallback)
			throws TranslationException;
}
