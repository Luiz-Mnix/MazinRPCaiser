package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.InputAction;
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

	@Override
	public void shutdown() {
		if(isOn()) {
			sHazelcastConnection.shutdown();
			sHazelcastConnection = null;
		}
	}

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

	@Override
	public void deleteContext(@Nonnull String contextId) {
		checkIfIsOn();
		sHazelcastConnection.getMap(contextId).clear();
	}

	@Nonnull
	@Override
	public <T> BlockingQueue<T> getCommandQueue(@Nonnull String commandName) {
		checkIfIsOn();
		return sHazelcastConnection.getQueue(commandName);
	}

	@Override
	public <T extends Serializable> void postNotification(String topicId, T data) {
		checkIfIsOn();
		ITopic<T> topic = sHazelcastConnection.getTopic(topicId);
		topic.publish(data);
	}

	@Nonnull
	@Override
	public <T> String addListener(@Nonnull MessageListener<T> listener, @Nonnull String topicId) {
		checkIfIsOn();
		ITopic<T> topic = sHazelcastConnection.getTopic(topicId);
		return topic.addMessageListener(listener);
	}

	@Override
	public void removeListener(@Nonnull String listenerId, @Nonnull String topicId) {
		checkIfIsOn();
		ITopic topic = sHazelcastConnection.getTopic(topicId);
		topic.removeMessageListener(listenerId);
	}
}
