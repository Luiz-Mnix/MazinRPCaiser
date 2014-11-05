package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.InputAction;
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
	void initialize();
	void shutdown();
	@Nonnull IContext retrieveContext(@Nonnull String contextId, boolean overwrites);
	void deleteContext(@Nonnull String contextId);
	@Nonnull <T> BlockingQueue<T> getCommandQueue(@Nonnull String commandName);
	<T extends Serializable> void postNotification(String topicId, T data);
	@Nonnull <T> String addListener(@Nonnull MessageListener<T> listener, @Nonnull String topicId);
	void removeListener(@Nonnull String listenerId, @Nonnull String topicId);
}
