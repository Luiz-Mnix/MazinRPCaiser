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
	protected Object processActionForReal(@Nonnull CloseSessionData actionData, @Nonnull IContext context,
												@Nonnull IDataGrid dataGrid) throws Exception {
		dataGrid.deleteContext(context.getId());
		return null;
	}
}
