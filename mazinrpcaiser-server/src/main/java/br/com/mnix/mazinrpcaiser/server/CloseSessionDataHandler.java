package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.CloseSessionData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by mnix05 on 11/4/14.
 *
 * @author mnix05
 */
@ActionHandler(to = CloseSessionData.class)
public class CloseSessionDataHandler extends DefaultDataHandler<CloseSessionData> {
	protected CloseSessionDataHandler() {
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
