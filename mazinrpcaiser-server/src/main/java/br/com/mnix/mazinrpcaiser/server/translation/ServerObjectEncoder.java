package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.common.ProxiedObject;
import br.com.mnix.mazinrpcaiser.common.exception.ObjectNotFoundException;
import br.com.mnix.mazinrpcaiser.common.translation.ITranslator;
import br.com.mnix.mazinrpcaiser.common.translation.TranslationException;
import br.com.mnix.mazinrpcaiser.server.data.IContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by mnix05 on 11/20/14.
 *
 * @author mnix05
 */
public class ServerObjectEncoder extends ContextObjectTranslator {
	public ServerObjectEncoder(@Nonnull IContext context) {
		super(context);
	}

	@Nonnull
	@Override
	public Object translate(@Nonnull Object data, @Nullable ITranslator fallback) throws TranslationException {
		if(data instanceof Serializable) {

			Serializable trueData = (Serializable) data;
			String objectId;

			try {
				objectId = getContext().getObjectId(trueData);
			} catch (ObjectNotFoundException ignored) {
				objectId = UUID.randomUUID().toString();
			}

			getContext().putObject(objectId, trueData);

			return new ProxiedObject(objectId, data.getClass());
		}

		throw new TranslationException("data cannot be serialized");
	}
}
