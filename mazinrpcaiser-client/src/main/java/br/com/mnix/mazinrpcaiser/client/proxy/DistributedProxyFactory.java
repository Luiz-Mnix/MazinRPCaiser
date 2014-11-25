package br.com.mnix.mazinrpcaiser.client.proxy;

import br.com.mnix.mazinrpcaiser.client.web.IServiceClient;
import br.com.mnix.mazinrpcaiser.common.DistributedObjectUtils;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.common.request.CreateObjectRequest;
import javassist.NotFoundException;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.WeakHashMap;

/**
 * Created by mnix05 on 11/19/14.
 *
 * @author mnix05
 */
public class DistributedProxyFactory implements IProxyFactory {
	@Nonnull private final IServiceClient mServiceClient;
	@Nonnull public IServiceClient getServiceClient() {
		return mServiceClient;
	}
	@Nonnull private final WeakHashMap<Integer, IProxy> mProxies = new WeakHashMap<>();

	public DistributedProxyFactory(@Nonnull IServiceClient serviceClient) {
		mServiceClient = serviceClient;
	}

	@Override
	@Nonnull
	public <T> T makeRemoteProxy(@Nonnull String id, boolean overwrites,
								 @Nonnull Class<T> distributedInterface, Serializable... args)
			throws Exception {
		DistributedObjectUtils.assertDistributedType(distributedInterface);
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
		return makeProxy(id, distributedInterface);
	}

	@Nonnull
	@Override
	public <T> T makeProxy(@Nonnull String id, @Nonnull Class<T> distributedInterface) {
		DistributedObjectUtils.assertDistributedType(distributedInterface);
		DistributedObjectProxy<T> proxy =
				new DistributedObjectProxy<>(distributedInterface, id, this, getServiceClient());
		mProxies.put(proxy.hashCode(), proxy);

		return proxy.proxyInterface();
	}

	@Nonnull
	@Override
	public IProxy getProxyOfObject(@Nonnull Object obj) throws NotFoundException {
		int proxyHashCode = obj.hashCode();

		if(mProxies.containsKey(proxyHashCode)) {
			return mProxies.get(proxyHashCode);
		}

		throw new NotFoundException("Proxy was not created");
	}
}
