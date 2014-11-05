package br.com.mnix.mazinrpcaiser.client;

import br.com.mnix.mazinrpcaiser.common.InputAction;
import br.com.mnix.mazinrpcaiser.common.ActionDataUtils;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MessageListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public class DataGridClient implements IDataGridClient {
	@Nullable private HazelcastInstance mHazelcastConnection = null;
	@Nonnull private final String mClusterAddress;

	public DataGridClient(@Nonnull String clusterAddress) {
		mClusterAddress = clusterAddress;
	}

	@Nonnull
	@Override
	public String getClusterAddress() {
		return mClusterAddress;
	}

	@Override
	public boolean isConnected() {
		return mHazelcastConnection != null;
	}

	@Override
	public void connect() throws ClusterUnavailableException {
		if(!isConnected()) {
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.getNetworkConfig().addAddress(mClusterAddress);
			try {
				mHazelcastConnection = HazelcastClient.newHazelcastClient(clientConfig);
			} catch(IllegalStateException e) {
				throw new ClusterUnavailableException(e);
			}
		}
	}

	@Override
	public void disconnect() {
		if(isConnected()) {
			mHazelcastConnection.shutdown();
			mHazelcastConnection = null;
		}
	}

	@Override
	public <T extends Serializable> void sendData(@Nonnull String repoId, @Nonnull T data) throws InterruptedException {
		checkState();
		BlockingQueue queue = (BlockingQueue) mHazelcastConnection.getQueue(repoId);
		queue.put(data);
	}

	@Nonnull
	@Override
	public String addListener(@Nonnull MessageListener listener, @Nonnull String topic) {
		checkState();
		return mHazelcastConnection.getTopic(topic).addMessageListener(listener);
	}

	@Override
	public void removeListener(@Nonnull String listenerId, @Nonnull String topic) {
		checkState();
		mHazelcastConnection.getTopic(topic).removeMessageListener(listenerId);
	}

	private void checkState() {
		if(!isConnected()) {
			throw new IllegalStateException("Connection to data grid must be established before sending actions.");
		}
	}
}
