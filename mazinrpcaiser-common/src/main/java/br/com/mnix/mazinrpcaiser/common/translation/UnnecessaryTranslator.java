package br.com.mnix.mazinrpcaiser.common.translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by mnix05 on 11/11/14.
 *
 * @author mnix05
 */
@Translator(of = {Number.class, Character.class, String.class, Class.class, Exception.class, Boolean.class})
public class UnnecessaryTranslator implements ITranslator {
	@Nonnull
	@Override
	public Object translate(@Nonnull Object data, @Nullable ITranslator fallback) {
		return data;
	}
}
