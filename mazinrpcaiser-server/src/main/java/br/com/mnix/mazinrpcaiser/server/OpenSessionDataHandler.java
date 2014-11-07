package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.OpenSessionData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
@ActionHandler(to = OpenSessionData.class)
public class OpenSessionDataHandler extends DefaultDataHandler<OpenSessionData> {

	protected OpenSessionDataHandler() {
		super(OpenSessionData.class);
	}

	@Nullable
	@Override
	protected Object processActionForReal(@Nonnull OpenSessionData actionData, @Nonnull IContext context,
										  @Nonnull IDataGrid dataGrid) throws Exception {
		dataGrid.retrieveContext(context.getId(), actionData.getOverwritesExisting());
		return null;
	}
}
