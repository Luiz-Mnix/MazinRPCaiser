package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.server.data.IContext;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;

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

	@Nullable protected abstract Object processActionForReal(@Nonnull T actionData, @Nonnull IContext context,
															 @Nonnull IDataGrid dataGrid) throws Exception;

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public Serializable processAction(@Nonnull RequestEnvelope action, @Nonnull IDataGrid dataGrid) throws Exception {
		if(action.getRequest().getClass().isAssignableFrom(mDataClass)) {
			// TODO translate data
			return (Serializable) processActionForReal(
					(T) action.getRequest(),
					dataGrid.retrieveContext(action.getSessionData().getContextId(), false),
					dataGrid
			);
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
