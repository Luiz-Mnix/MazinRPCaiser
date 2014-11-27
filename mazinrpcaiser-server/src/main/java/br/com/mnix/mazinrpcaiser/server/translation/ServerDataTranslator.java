package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.common.translation.TranslationException;
import br.com.mnix.mazinrpcaiser.server.data.IContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

import static br.com.mnix.mazinrpcaiser.common.translation.DataTranslator.translateData;

/**
 * Created by mnix05 on 11/20/14.
 *
 * @author mnix05
 */
public final class ServerDataTranslator {
	@Nullable
	public static Serializable encode(@Nullable Serializable data, @Nonnull IContext context)
			throws TranslationException {
		return (Serializable) translateData(data, new ServerObjectEncoder(context));
	}

	@Nullable
	public static Serializable decode(@Nullable Serializable[] args, @Nonnull IContext context)
			throws TranslationException {
		if(args == null) {
			return null;
		}

		Serializable[] translatedArgs = new Serializable[args.length];

		for (int idx = 0; idx < args.length; ++idx) {
			translatedArgs[idx] = (Serializable) translateData(args[idx], new ClientObjectDecoder(context));
		}
		return translatedArgs;
	}
}
