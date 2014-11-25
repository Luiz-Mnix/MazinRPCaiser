package br.com.mnix.mazinrpcaiser.client.translation;

import br.com.mnix.mazinrpcaiser.client.proxy.IProxyFactory;
import br.com.mnix.mazinrpcaiser.common.ProxiedObject;
import br.com.mnix.mazinrpcaiser.common.exception.IllegalDistributedTypeException;
import br.com.mnix.mazinrpcaiser.common.translation.ITranslator;
import br.com.mnix.mazinrpcaiser.common.translation.TranslationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by mnix05 on 11/21/14.
 *
 * @author mnix05
 */
public class ServerObjectDecoder implements ITranslator {
	@Nonnull private final IProxyFactory mProxyFactory;
	@Nonnull public IProxyFactory getProxyFactory() {
		return mProxyFactory;
	}

	public ServerObjectDecoder(@Nonnull IProxyFactory proxyFactory) {
		mProxyFactory = proxyFactory;
	}

	@Nonnull
	@Override
	public Object translate(@Nonnull Object data, @Nullable ITranslator fallback) throws TranslationException {
		if(data instanceof ProxiedObject) {
			ProxiedObject proxyRef = (ProxiedObject) data;
			try {
				return getProxyFactory().makeProxy(proxyRef.getObjectId(), proxyRef.getObjectClass());
			} catch (IllegalDistributedTypeException e) {
				throw new TranslationException(e);
			}
		}

		throw new TranslationException(new IllegalArgumentException("data is not a "
				+ ProxiedObject.class.getCanonicalName()));
	}
}
