package br.com.mnix.mazinrpcaiser.client;

import com.hazelcast.core.MessageListener;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public interface IDataGridClient {
	@Nonnull String getClusterAddress();
	boolean isConnected();
	void connect() throws ClusterUnavailableException;
	void disconnect();
	<T extends Serializable> void sendData(@Nonnull String repoId, @Nonnull T data) throws InterruptedException;
	@Nonnull String addListener(@Nonnull MessageListener listener, @Nonnull String topic);
	void removeListener(@Nonnull String listenerId, @Nonnull String topic);
}
