package br.com.mnix.mazinrpcaiser.client.web;

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
	void connect() throws DataGridUnavailableException;
	void disconnect();
	<T extends Serializable> void sendData(@Nonnull T data) throws InterruptedException;
	@Nonnull <T> String addListener(@Nonnull String topicId, @Nonnull MessageListener<T> listener);
	void removeListener(@Nonnull String topicId, @Nonnull String listenerId);
}
