package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.common.ProxiedObject;
import br.com.mnix.mazinrpcaiser.common.exception.ObjectNotFoundException;
import br.com.mnix.mazinrpcaiser.common.translation.ITranslator;
import br.com.mnix.mazinrpcaiser.common.translation.TranslationException;
import br.com.mnix.mazinrpcaiser.server.data.IContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by mnix05 on 11/11/14.
 *
 * @author mnix05
 */
public class ClientObjectDecoder extends ContextObjectTranslator {
	public ClientObjectDecoder(@Nonnull IContext context) {
		super(context);
	}

	@Nonnull
	@Override
	public Object translate(@Nonnull Object data, @Nullable ITranslator fallback)
			throws TranslationException {
		assert data instanceof ProxiedObject;

		try {
			return getContext().getSerializable(((ProxiedObject) data).getObjectId());
		} catch(ObjectNotFoundException e) {
			throw new TranslationException(e);
		}
	}
}
