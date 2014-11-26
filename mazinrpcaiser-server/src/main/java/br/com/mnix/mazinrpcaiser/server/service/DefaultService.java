package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.server.data.IContext;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import br.com.mnix.mazinrpcaiser.server.translation.ServerDataTranslator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public abstract class DefaultService<T extends Serializable> implements IService {
	@Nonnull protected final Class<T> mDataClass;

	protected DefaultService(@Nonnull Class<T> dataClass) {
		mDataClass = dataClass;
	}

	@Nullable protected abstract Serializable processRequestImpl(@Nonnull T request, @Nonnull IContext context,
																 @Nonnull IDataGrid dataGrid) throws Throwable;

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public Serializable processRequest(@Nonnull RequestEnvelope requestEnv, @Nonnull IDataGrid dataGrid)
			throws Throwable {
		if(requestEnv.getRequest().getClass().isAssignableFrom(mDataClass)) {
			IContext context = dataGrid.retrieveContext(requestEnv.getSessionData().getContextId(), false);
			Serializable processedData = processRequestImpl((T) requestEnv.getRequest(), context, dataGrid);

			return ServerDataTranslator.encode(processedData, context);
		}

		throw new IllegalArgumentException(
				String.format(
						"%s can only handle actions of type %s",
						DefaultService.class.getName(),
						mDataClass.getName()
				)
		);
	}
}
