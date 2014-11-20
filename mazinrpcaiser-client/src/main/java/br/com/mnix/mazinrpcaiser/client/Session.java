package br.com.mnix.mazinrpcaiser.client;

import br.com.mnix.mazinrpcaiser.common.SessionData;
import br.com.mnix.mazinrpcaiser.common.request.CloseSessionRequest;
import br.com.mnix.mazinrpcaiser.common.request.OpenSessionRequest;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

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

	@Nonnull private final IDataGridClient mDataGridClient;
	@Nonnull public IDataGridClient getDataGridClient() {
		return mDataGridClient;
	}

	@SuppressWarnings("FieldAccessedSynchronizedAndUnsynchronized")
	@Nullable private IServiceClient mServiceClient = null;

	@Nonnull private final Object mProxyFactoryLock = new Object();
	@Nullable private volatile IProxyFactory mProxyFactory = null;
	@Nonnull public IProxyFactory getProxyFactory() {
		if(!isOpened()) {
			throw new IllegalStateException("Session must be opened first.");
		}
		// Double-checked singleton
		IProxyFactory singleton = mProxyFactory;
		if(singleton == null) {
			synchronized (mProxyFactoryLock) {
				singleton = mProxyFactory;
				if(singleton == null) {
					assert mServiceClient != null;
					singleton = new ProxyFactory(mServiceClient);
					mProxyFactory = singleton;
				}
			}
		}
		return singleton;
	}

	public boolean isOpened() {
		return mDataGridClient.isConnected() && mServiceClient != null;
	}

	public Session(@Nonnull String contextId, @Nonnull String serverAddress) {
		mSessionData = new SessionData(contextId, serverAddress);
		mDataGridClient = DataGridClientFactory.createDataGrid(serverAddress);
	}

	public Session(@Nonnull String serverAddress) {
		this(UUID.randomUUID().toString(), serverAddress);
	}

	public void open(boolean overwritesExisting)
			throws DataGridUnavailableException, ServerExecutionException, InterruptedException {
		if(!isOpened()) {
			mDataGridClient.connect();
			mServiceClient = new ServiceClient(getSessionData(), mDataGridClient);
			mServiceClient.makeRequest(new OpenSessionRequest(overwritesExisting));
		}
	}

	@SuppressWarnings("ConstantConditions")
	public void invalidate(boolean wipesContext) throws ServerExecutionException, InterruptedException {
		if(isOpened()) {
			mServiceClient.makeRequest(new CloseSessionRequest(wipesContext));
			mServiceClient = null;
			mDataGridClient.disconnect();
		}
	}
}
