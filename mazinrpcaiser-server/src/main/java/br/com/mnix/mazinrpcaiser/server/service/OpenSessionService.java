package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.request.OpenSessionRequest;
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
@Service(forRequest = OpenSessionRequest.class)
public class OpenSessionService extends DefaultService<OpenSessionRequest> {
	public OpenSessionService() {
		super(OpenSessionRequest.class);
	}

	@Nullable
	@Override
	protected Serializable processRequestImpl(@Nonnull OpenSessionRequest request, @Nonnull IContext context,
											  @Nonnull IDataGrid dataGrid) throws Exception {
		dataGrid.retrieveContext(context.getId(), request.getOverwritesExisting());
		return null;
	}
}
