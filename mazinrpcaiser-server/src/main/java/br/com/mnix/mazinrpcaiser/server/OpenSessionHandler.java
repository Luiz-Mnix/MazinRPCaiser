package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.InputAction;
import br.com.mnix.mazinrpcaiser.common.OpenSessionData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public class OpenSessionHandler extends DefaultDataHandler<OpenSessionData> {

	protected OpenSessionHandler() {
		super(OpenSessionData.class);
	}

	@Nullable
	@Override
	protected Serializable processActionForReal(@Nonnull InputAction action, @Nonnull OpenSessionData actionData, @Nonnull IDataGrid dataGrid) {
		dataGrid.retrieveContext(action.getSessionMetadata().getContextId(), actionData.getOverwritesExisting());
		return null;
	}
}
