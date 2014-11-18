package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.common.NotTransmissibleObject;
import br.com.mnix.mazinrpcaiser.common.exception.ObjectNotFoundException;
import br.com.mnix.mazinrpcaiser.server.data.IContext;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by mnix05 on 11/11/14.
 *
 * @author mnix05
 */
public class ProxiedObjectTranslator implements ITranslator {
	@Nonnull
	@Override
	public Serializable translate(@Nonnull Serializable data, @Nonnull IContext context) throws TranslationException {
		String objectId;

		if(context.containsObject(data)) {
			try {
				objectId = context.getObjectId(data);
			} catch (ObjectNotFoundException ignored) {
				objectId = StringUtils.EMPTY; // Unreachable code, but it's here to hide IntelliJ warnings.
			}
		} else {
			objectId = UUID.randomUUID().toString();
			context.putObject(objectId, data);
		}

		return new NotTransmissibleObject(objectId, data.getClass());
	}
}
