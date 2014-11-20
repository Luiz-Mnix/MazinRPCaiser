package br.com.mnix.mazinrpcaiser.client;

import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.common.request.CreateObjectRequest;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by mnix05 on 11/19/14.
 *
 * @author mnix05
 */
public class ProxyFactory implements IProxyFactory {
	@Nonnull private final IServiceClient mServiceClient;
	@Nonnull public IServiceClient getServiceClient() {
		return mServiceClient;
	}

	public ProxyFactory(@Nonnull IServiceClient serviceClient) {
		mServiceClient = serviceClient;
	}

	@Override
	@Nonnull
	public <T> T getProxy(@Nonnull String id, boolean overwrites, @Nonnull Class<T> distributedInterface, Serializable... args)
			throws Exception {
		CreateObjectRequest request = new CreateObjectRequest(id, distributedInterface, null, args);
		try {
			getServiceClient().makeRequest(request);
		} catch(ServerExecutionException ex) {
			if(ex.getCause() instanceof Exception) {
				throw (Exception) ex.getCause();
			} else {
				throw (Error) ex.getCause();
			}
		}
		return DistributedObjectProxy.forInterface(distributedInterface, id, getServiceClient());
	}

	public <T> T getProxy(Class<T> distributedInterface, Serializable... args) throws Exception {
		return getProxy(UUID.randomUUID().toString(), true, distributedInterface, args);
	}
}
