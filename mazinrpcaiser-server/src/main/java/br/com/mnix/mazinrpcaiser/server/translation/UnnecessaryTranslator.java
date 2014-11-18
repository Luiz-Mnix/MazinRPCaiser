package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.server.data.IContext;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/11/14.
 *
 * @author mnix05
 */
@Translator(of = {Number.class, Character.class, String.class, Class.class, Exception.class})
public class UnnecessaryTranslator implements ITranslator {
	@Nonnull
	@Override
	public Serializable translate(@Nonnull Serializable data, @Nonnull IContext context) {
		return data;
	}
}