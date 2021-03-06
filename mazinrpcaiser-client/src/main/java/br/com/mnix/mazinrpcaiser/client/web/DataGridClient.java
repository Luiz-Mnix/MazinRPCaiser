package br.com.mnix.mazinrpcaiser.client.web;

import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;
import org.apache.commons.lang3.StringUtils;

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
	public void connect() throws DataGridUnavailableException {
		if(!isConnected()) {
			ClientConfig clientConfig = new ClientConfig();

			String[] components = StringUtils.split(mClusterAddress, ':');

			if(components.length == 1) {

				clientConfig.getNetworkConfig().addAddress(mClusterAddress);
			}
			try {
				mHazelcastConnection = HazelcastClient.newHazelcastClient(clientConfig);
			} catch(IllegalStateException e) {
				throw new DataGridUnavailableException(e);
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
	public <T extends Serializable> void sendData(@Nonnull T data) throws InterruptedException {
		checkState();
		BlockingQueue<T> queue = mHazelcastConnection.getQueue(MazinRPCaiserConstants.COMMAND_QUEUE_ID);
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
