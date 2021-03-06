package br.com.mnix.mazinrpcaiser.server.data;

import com.hazelcast.core.MessageListener;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public interface IDataGrid {
	boolean isOn();
	void raise();
	void shutdown();

	@Nonnull <T> BlockingQueue<T> getCommandQueue(@Nonnull String commandName);

	@Nonnull IContext retrieveContext(@Nonnull String contextId, boolean overwrites);
	void deleteContext(@Nonnull String contextId);

	<T extends Serializable> void postNotification(String topicId, T data);
	@Nonnull <T> String addListener(@Nonnull String topicId, @Nonnull MessageListener<T> listener);
	void removeListener(@Nonnull String topicId, @Nonnull String listenerId);
}
