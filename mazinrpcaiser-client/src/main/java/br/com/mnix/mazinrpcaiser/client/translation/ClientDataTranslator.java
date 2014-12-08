package br.com.mnix.mazinrpcaiser.client.translation;

import br.com.mnix.mazinrpcaiser.client.proxy.IProxyFactory;
import br.com.mnix.mazinrpcaiser.common.translation.TranslationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

import static br.com.mnix.mazinrpcaiser.common.translation.DataTranslator.translateData;

/**
 * Created by mnix05 on 11/21/14.
 *
 * @author mnix05
 */
public final class ClientDataTranslator {
	private ClientDataTranslator() {}

	@Nullable
	public static Serializable encodeToServer(@Nullable Object[] args, @Nonnull IProxyFactory proxyFactory)
			throws TranslationException {
		if(args == null) {
			return null;
		}

		ClientObjectEncoder fallback = new ClientObjectEncoder(proxyFactory);
		Serializable[] translatedArgs = new Serializable[args.length];

		for (int idx = 0; idx < args.length; ++idx) {
			translatedArgs[idx] = (Serializable) translateData(args[idx], fallback);
		}
		return translatedArgs;
	}

	@Nullable
	public static Object decodeFromServer(@Nullable Serializable data, @Nonnull IProxyFactory proxyFactory)
			throws TranslationException {
		return translateData(data, new ServerObjectDecoder(proxyFactory));
	}
}
