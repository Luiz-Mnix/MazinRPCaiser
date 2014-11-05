package br.com.mnix.mazinrpcaiser.client;

import br.com.mnix.mazinrpcaiser.common.CloseSessionData;
import br.com.mnix.mazinrpcaiser.common.OpenSessionData;
import br.com.mnix.mazinrpcaiser.common.SessionMetadata;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public final class Session {
	@Nonnull private final SessionMetadata mSessionMetadata;
	@Nonnull public SessionMetadata getSessionMetadata() {
		return mSessionMetadata;
	}

	@Nonnull private final IDataGridClient mDataGrid;
	@Nullable private ServiceClient mServiceClient = null;

	public boolean isOpened() {
		return mDataGrid.isConnected() && mServiceClient != null;
	}

	public Session(@Nonnull String contextId, @Nonnull String serverAddress) {
		mSessionMetadata = new SessionMetadata(contextId, serverAddress);
		mDataGrid = DataGridClientFactory.createDataGrid(serverAddress);
	}

	public Session(@Nonnull String serverAddress) {
		this(java.util.UUID.randomUUID().toString(), serverAddress);
	}

	public void open(boolean overwritesExisting)
			throws ClusterUnavailableException, ServerExecutionException, InterruptedException {
		if(!isOpened()) {
			mDataGrid.connect();
			mServiceClient = new ServiceClient(mDataGrid);
			mServiceClient.requestAction(new OpenSessionData(overwritesExisting), getSessionMetadata());
		}
	}

	@SuppressWarnings("ConstantConditions")
	public void invalidate() throws ServerExecutionException, InterruptedException {
		if(isOpened()) {
			mServiceClient.requestAction(new CloseSessionData(), getSessionMetadata());
			mServiceClient = null;
			mDataGrid.disconnect();
		}
	}
}
