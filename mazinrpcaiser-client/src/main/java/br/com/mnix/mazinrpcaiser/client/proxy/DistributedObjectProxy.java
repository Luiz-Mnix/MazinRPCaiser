package br.com.mnix.mazinrpcaiser.client.proxy;

import br.com.mnix.mazinrpcaiser.client.web.IServiceClient;
import br.com.mnix.mazinrpcaiser.client.translation.ClientDataTranslator;
import br.com.mnix.mazinrpcaiser.common.DistributedObjectUtils;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.common.request.MethodRequest;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mnix05 on 11/19/14.
 *
 * @author mnix05
 */
public class DistributedObjectProxy<T> implements IProxy {
	@Nonnull public static final Class<?>[] EMPTY_TYPES = new Class<?>[0];
	@Nonnull public static final Object[] EMPTY_ARGS = new Object[0];

	@Nonnull private final Class<T> mBaseInterface;
	@Nonnull private final String mId;
	@Nonnull private final IProxyFactory mParentFactory;
	@Nonnull private final IServiceClient mClient;
	@Nonnull private final Set<Method> mApprovedMethods = new HashSet<>();

	public DistributedObjectProxy(@Nonnull Class<T> baseInterface, @Nonnull String id,
								  @Nonnull IProxyFactory parentFactory, @Nonnull IServiceClient client) {
		if (!baseInterface.isInterface()) {
			throw new AssertionError("Precondition failed");
		}

		mBaseInterface = baseInterface;
		mId = id;
		mClient = client;
		mParentFactory = parentFactory;
	}

	@Override
	@Nonnull public T proxyInterface() {
		javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();
		factory.setInterfaces(new Class[] { getBaseInterface() });

		try {
			//noinspection unchecked
			return (T) factory.create(EMPTY_TYPES, EMPTY_ARGS, this);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			throw new RuntimeException("Unable to create proxy", e);
		}
	}

	@Override
	@Nullable
	public Object invoke(Object o, Method method, Method method2, Object[] objects) throws Throwable {
		if(ArrayUtils.contains(Object.class.getDeclaredMethods(), method)) {
			return method.invoke(this, objects);
		}

		if(!mApprovedMethods.contains(method)) {
			if(DistributedObjectUtils.isDistributedMethodMapped(method)) {
				mApprovedMethods.add(method);
			} else {
				throw new UnsupportedOperationException(
						"There's is no backend equivalent for method "+ method.toString()
				);
			}
		}

		MethodRequest request = new MethodRequest(
				mId,
				method.getName(),
				(Serializable[]) ClientDataTranslator.encodeToServer(objects, mParentFactory)
		);

		try {
			return ClientDataTranslator.decodeFromServer(mClient.makeRequest(request), mParentFactory);
		} catch(ServerExecutionException e) {
			throw e.getCause();
		}
	}

	@Override
	@Nonnull
	public Class<?> getBaseInterface() {
		return mBaseInterface;
	}

	@Override
	@Nonnull
	public String getId() {
		return mId;
	}
}
