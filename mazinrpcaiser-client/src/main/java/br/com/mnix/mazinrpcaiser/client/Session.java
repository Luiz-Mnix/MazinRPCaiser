package br.com.mnix.mazinrpcaiser.client;

import br.com.mnix.mazinrpcaiser.client.proxy.DistributedProxyFactory;
import br.com.mnix.mazinrpcaiser.client.proxy.IProxyFactory;
import br.com.mnix.mazinrpcaiser.client.web.*;
import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;
import br.com.mnix.mazinrpcaiser.common.SessionData;
import br.com.mnix.mazinrpcaiser.common.request.CloseSessionRequest;
import br.com.mnix.mazinrpcaiser.common.request.OpenSessionRequest;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
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
					singleton = new DistributedProxyFactory(mServiceClient);
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

	public Session() {
		this(UUID.randomUUID().toString(), MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS);
	}

	public void open(boolean overwritesExisting)
			throws Exception {
		if(!isOpened()) {
			mDataGridClient.connect();
			mServiceClient = new ServiceClient(getSessionData(), mDataGridClient);

			try {
				makeRequest(new OpenSessionRequest(overwritesExisting));
			} catch(Exception ignored) {
				mDataGridClient.disconnect();
				mServiceClient = null;
			}
		}
	}

	public void invalidate(boolean wipesContext) throws Exception {
		if(isOpened()) {
			try {
				makeRequest(new CloseSessionRequest(wipesContext));
			} finally {
				mServiceClient = null;
				mDataGridClient.disconnect();
			}
		}
	}

	private void makeRequest(@Nonnull Serializable request) throws Exception {
		try {
			assert mServiceClient != null;
			mServiceClient.makeRequest(request);
		} catch (ServerExecutionException e) {
			if(e.getCause() instanceof Exception) {
				throw (Exception) e.getCause();
			} else {
				assert e.getCause() instanceof Error;
				throw (Error) e.getCause();
			}
		}
	}
}
