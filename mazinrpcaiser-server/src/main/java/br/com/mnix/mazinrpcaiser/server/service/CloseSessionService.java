package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.request.CloseSessionRequest;
import br.com.mnix.mazinrpcaiser.server.data.IContext;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by mnix05 on 11/4/14.
 *
 * @author mnix05
 */
@Service(forRequest = CloseSessionRequest.class)
public class CloseSessionService extends DefaultService<CloseSessionRequest> {
	protected CloseSessionService() {
		super(CloseSessionRequest.class);
	}

	@Nullable
	@Override
	protected Object processActionForReal(@Nonnull CloseSessionRequest actionData, @Nonnull IContext context,
												@Nonnull IDataGrid dataGrid) throws Exception {
		dataGrid.deleteContext(context.getId());
		return null;
	}
}