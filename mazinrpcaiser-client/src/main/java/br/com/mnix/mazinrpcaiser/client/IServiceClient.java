package br.com.mnix.mazinrpcaiser.client;

import br.com.mnix.mazinrpcaiser.common.SessionData;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.common.request.IReturn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/7/14.
 *
 * @author mnix05
 */
public interface IServiceClient {
	@Nonnull SessionData getSessionData();

	@Nullable Serializable makeRequest(@Nonnull Serializable request, int timeout)
			throws InterruptedException, ServerExecutionException;

	@Nullable Serializable makeRequest(@Nonnull Serializable actionData)
			throws ServerExecutionException, InterruptedException;

	@Nullable <T extends Serializable> T makeRequest(@Nonnull IReturn<T> actionData)
			throws ServerExecutionException, InterruptedException;
}
