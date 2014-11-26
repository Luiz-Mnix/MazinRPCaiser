package br.com.mnix.mazinrpcaiser.client.translation;

import br.com.mnix.mazinrpcaiser.client.proxy.IProxy;
import br.com.mnix.mazinrpcaiser.client.proxy.IProxyFactory;
import br.com.mnix.mazinrpcaiser.common.ProxiedObject;
import br.com.mnix.mazinrpcaiser.common.translation.ITranslator;
import br.com.mnix.mazinrpcaiser.common.translation.TranslationException;
import javassist.NotFoundException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by mnix05 on 11/21/14.
 *
 * @author mnix05
 */
public class ClientObjectEncoder implements ITranslator {
	@Nonnull private final IProxyFactory mProxyFactory;
	@Nonnull public IProxyFactory getProxyFactory() {
		return mProxyFactory;
	}

	public ClientObjectEncoder(@Nonnull IProxyFactory proxyFactory) {
		mProxyFactory = proxyFactory;
	}

	@Nonnull
	@Override
	public Object translate(@Nonnull Object data, @Nullable ITranslator fallback) throws TranslationException {
		IProxy trueData;

		try {
			trueData = getProxyFactory().getProxyOfObject(data);
		} catch (NotFoundException e) {
			throw new TranslationException(e);
		}

		return new ProxiedObject(trueData.getId(), trueData.getBaseInterface());
	}
}
