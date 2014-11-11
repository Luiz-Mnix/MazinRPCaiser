package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.common.NotTransmissibleObject;
import br.com.mnix.mazinrpcaiser.server.data.IContext;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by mnix05 on 11/11/14.
 *
 * @author mnix05
 */
public class ProxiedObjectTranslator implements ITranslator {
	@Override
	public Serializable translate(Serializable data, IContext context) throws TranslationException {
		String objectId;

		if(context.containsObject(data)) {
			objectId = context.getObjectId(data);
		} else {
			objectId = UUID.randomUUID().toString();
			context.putObject(objectId, data);
		}

		return new NotTransmissibleObject(objectId, data.getClass());
	}
}
