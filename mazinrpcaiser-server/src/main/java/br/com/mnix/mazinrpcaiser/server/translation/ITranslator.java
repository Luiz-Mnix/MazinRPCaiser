package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.server.data.IContext;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/11/14.
 *
 * @author mnix05
 */
public interface ITranslator {
	@Nonnull Serializable translate(@Nonnull Serializable data, @Nonnull IContext context)
			throws TranslationException;
}
