package br.com.mnix.mazinrpcaiser.client;

import br.com.mnix.mazinrpcaiser.common.DistributedObjectUtils;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.common.request.MethodRequest;
import javassist.util.proxy.MethodHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mnix05 on 11/19/14.
 *
 * @author mnix05
 */
public class DistributedObjectProxy implements MethodHandler {
	@Nonnull public static final Class<?>[] EMPTY_TYPES = new Class<?>[0];
	@Nonnull public static final Object[] EMPTY_ARGS = new Object[0];
	@Nonnull public static <T> T forInterface(@Nonnull Class<T> distributedInterface, @Nonnull String id,
											  @Nonnull IServiceClient client) {
		javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();

		if (distributedInterface.isInterface()) {
			factory.setInterfaces(new Class[] { distributedInterface });
		} else {
			throw new AssertionError("Precondition failed");
		}

		try {
			//noinspection unchecked
			return (T) factory.create(
					EMPTY_TYPES, EMPTY_ARGS, new DistributedObjectProxy(id, client)
			);
		} catch (NoSuchMethodException | InstantiationException
				| IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("Unable to create proxy", e);
		}
	}

	@Nonnull private final IServiceClient mClient;
	@Nonnull public IServiceClient getClient() {
		return mClient;
	}

	@Nonnull private final String mId;
	@Nonnull public String getId() {
		return mId;
	}

	@Nonnull private final Set<Method> mApprovedMethods = new HashSet<>();

	public DistributedObjectProxy(@Nonnull String id, @Nonnull IServiceClient client) {
		mId = id;
		mClient = client;
	}

	@Override
	@Nullable
	public Object invoke(Object o, Method method, Method method2, Object[] objects) throws Throwable {
		if(!mApprovedMethods.contains(method)) {
			if(DistributedObjectUtils.isDistributedMethodMapped(method)) {
				mApprovedMethods.add(method);
			} else {
				throw new UnsupportedOperationException(
						"There's is no backend equivalent for method "+ method.toString());
			}
		}

		MethodRequest request = new MethodRequest(getId(), method.getName(), objects);

		try {
			// TODO translate
			return getClient().makeRequest(request);
		} catch(ServerExecutionException e) {
			throw e.getCause();
		}
	}
}
