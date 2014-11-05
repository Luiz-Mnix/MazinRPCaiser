package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.IActionData;
import br.com.mnix.mazinrpcaiser.common.InputAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public abstract class DefaultDataHandler<T extends IActionData> implements IActionHandler {
	@Nonnull protected final Class<T> mDataClass;

	protected DefaultDataHandler(@Nonnull Class<T> dataClass) {
		mDataClass = dataClass;
	}

	@Nullable protected abstract Serializable processActionForReal(@Nonnull InputAction action, @Nonnull T actionData,
																   @Nonnull IDataGrid dataGrid);

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public Serializable processAction(@Nonnull InputAction action, @Nonnull IDataGrid dataGrid) throws Exception {
		if(action.getActionData().getClass().isAssignableFrom(mDataClass)) {
			return processActionForReal(action, (T) action.getActionData(), dataGrid);
		}

		throw new IllegalArgumentException(
				String.format(
						"%s can only handle actions of type %s",
						DefaultDataHandler.class.getName(),
						mDataClass.getName()
				)
		);
	}
}
