package br.com.mnix.mazinrpcaiser.client;

import br.com.mnix.mazinrpcaiser.common.SessionData;
import br.com.mnix.mazinrpcaiser.common.request.CloseSessionRequest;
import br.com.mnix.mazinrpcaiser.common.request.OpenSessionRequest;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public final class Session {
	@Nonnull private final SessionData mSessionData;
	@Nonnull public SessionData getSessionData() {
		return mSessionData;
	}

	@Nonnull private final IDataGridClient mDataGrid;
	@Nullable private IServiceClient mServiceClient = null;

	public boolean isOpened() {
		return mDataGrid.isConnected() && mServiceClient != null;
	}

	public Session(@Nonnull String contextId, @Nonnull String serverAddress) {
		mSessionData = new SessionData(contextId, serverAddress);
		mDataGrid = DataGridClientFactory.createDataGrid(serverAddress);
	}

	public Session(@Nonnull String serverAddress) {
		this(java.util.UUID.randomUUID().toString(), serverAddress);
	}

	public void open(boolean overwritesExisting)
			throws DataGridUnavailableException, ServerExecutionException, InterruptedException {
		if(!isOpened()) {
			mDataGrid.connect();
			mServiceClient = new ServiceClient(mDataGrid);
			mServiceClient.makeRequest(new OpenSessionRequest(overwritesExisting), getSessionData());
		}
	}

	@SuppressWarnings("ConstantConditions")
	public void invalidate() throws ServerExecutionException, InterruptedException {
		if(isOpened()) {
			mServiceClient.makeRequest(new CloseSessionRequest(), getSessionData());
			mServiceClient = null;
			mDataGrid.disconnect();
		}
	}
}
