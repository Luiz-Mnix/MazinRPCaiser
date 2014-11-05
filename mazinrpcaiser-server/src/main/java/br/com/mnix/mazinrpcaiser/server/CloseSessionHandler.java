package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.CloseSessionData;
import br.com.mnix.mazinrpcaiser.common.InputAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/4/14.
 *
 * @author mnix05
 */
public class CloseSessionHandler extends DefaultDataHandler<CloseSessionData> {
	protected CloseSessionHandler() {
		super(CloseSessionData.class);
	}

	@Nullable
	@Override
	protected Serializable processActionForReal(@Nonnull InputAction action, @Nonnull CloseSessionData actionData,
												@Nonnull IDataGrid dataGrid) {
		dataGrid.retrieveContext(action.getSessionMetadata().getContextId(), true);
		return null;
	}
}
