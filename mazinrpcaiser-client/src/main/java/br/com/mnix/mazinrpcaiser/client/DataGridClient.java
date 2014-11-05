package br.com.mnix.mazinrpcaiser.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
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

	@SuppressWarnings("ConstantConditions")
	@Override
	public void disconnect() {
		if(isConnected()) {
			mHazelcastConnection.shutdown();
			mHazelcastConnection = null;
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public <T extends Serializable> void sendData(@Nonnull String repoId, @Nonnull T data) throws InterruptedException {
		checkState();
		BlockingQueue<T> queue = mHazelcastConnection.getQueue(repoId);
		queue.put(data);
	}

	@SuppressWarnings("ConstantConditions")
	@Nonnull
	@Override
	public <T> String addListener(@Nonnull String topicId, @Nonnull MessageListener<T> listener) {
		checkState();
		ITopic<T> topic = mHazelcastConnection.getTopic(topicId);
		return topic.addMessageListener(listener);
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void removeListener(@Nonnull String topicId, @Nonnull String listenerId) {
		checkState();
		ITopic topic = mHazelcastConnection.getTopic(topicId);
		topic.removeMessageListener(listenerId);
	}

	private void checkState() {
		if(!isConnected()) {
			throw new IllegalStateException("Connection to data grid must be established before sending actions.");
		}
	}
}
