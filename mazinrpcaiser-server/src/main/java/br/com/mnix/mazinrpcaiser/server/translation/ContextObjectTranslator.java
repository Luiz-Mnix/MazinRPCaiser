package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.common.translation.ITranslator;
import br.com.mnix.mazinrpcaiser.server.data.IContext;

import javax.annotation.Nonnull;

/**
 * Created by mnix05 on 11/20/14.
 *
 * @author mnix05
 */
public abstract class ContextObjectTranslator implements ITranslator {
	@Nonnull private final IContext mContext;
	@Nonnull public IContext getContext() {
	    return mContext;
	}

	protected ContextObjectTranslator(@Nonnull IContext context) {
		mContext = context;
	}
}
