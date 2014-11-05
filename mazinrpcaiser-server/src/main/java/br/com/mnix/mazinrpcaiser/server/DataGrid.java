package br.com.mnix.mazinrpcaiser.server;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public class DataGrid implements IDataGrid {
	@Nullable
	private static HazelcastInstance sHazelcastConnection = null;

	public DataGrid() {
	}

	private void checkIfIsOn() {
		if(!isOn()) {
			throw new IllegalStateException("Data grid must be initialized before it can be worked with.");
		}
	}

	@Override
	public boolean isOn() {
		return sHazelcastConnection != null;
	}

	@Override
	public void initialize() {
		if(!isOn()) {
			Config config = new Config();
			config.getNetworkConfig().getInterfaces().addInterface("127.0.0.1");
			sHazelcastConnection = Hazelcast.newHazelcastInstance(config);
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void shutdown() {
		if(isOn()) {
			sHazelcastConnection.shutdown();
			sHazelcastConnection = null;
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Nonnull
	@Override
	public IContext retrieveContext(@Nonnull String contextId, boolean overwrites) {
		checkIfIsOn();
		Map<String, Serializable> context = sHazelcastConnection.getMap(contextId);

		if(overwrites) {
			context.clear();
		}

		return new MapContext(context);
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void deleteContext(@Nonnull String contextId) {
		checkIfIsOn();
		sHazelcastConnection.getMap(contextId).clear();
	}

	@SuppressWarnings("ConstantConditions")
	@Nonnull
	@Override
	public <T> BlockingQueue<T> getCommandQueue(@Nonnull String commandName) {
		checkIfIsOn();
		return sHazelcastConnection.getQueue(commandName);
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public <T extends Serializable> void postNotification(String topicId, T data) {
		checkIfIsOn();
		ITopic<T> topic = sHazelcastConnection.getTopic(topicId);
		topic.publish(data);
	}

	@SuppressWarnings("ConstantConditions")
	@Nonnull
	@Override
	public <T> String addListener(@Nonnull String topicId, @Nonnull MessageListener<T> listener) {
		checkIfIsOn();
		ITopic<T> topic = sHazelcastConnection.getTopic(topicId);
		return topic.addMessageListener(listener);
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void removeListener(@Nonnull String topicId, @Nonnull String listenerId) {
		checkIfIsOn();
		ITopic topic = sHazelcastConnection.getTopic(topicId);
		topic.removeMessageListener(listenerId);
	}
}
