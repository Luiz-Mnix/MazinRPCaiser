package br.com.mnix.mazinrpcaiser.client.proxy;

import br.com.mnix.mazinrpcaiser.client.web.IServiceClient;
import br.com.mnix.mazinrpcaiser.common.DistributedVersion;
import br.com.mnix.mazinrpcaiser.common.ITransmissible;
import br.com.mnix.mazinrpcaiser.common.ProxiedObject;
import br.com.mnix.mazinrpcaiser.common.SessionData;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.common.request.IReturn;
import br.com.mnix.mazinrpcaiser.common.request.MethodRequest;
import javassist.NotFoundException;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by mnix05 on 11/20/14.
 *
 * @author mnix05
 */
public class DistributedObjectProxyTest {
	private static final String DEFAULT_ID = "foo";

	@SuppressWarnings("ExpectedExceptionNeverThrown") // It IS thrown, dumbAss JUnit
	@Test(expected = IllegalAccessException.class)
	public void testGetProxy_ServerSideException() throws Throwable {
		// Arrange
		final IProxyFactory factory = new MockProxyFactory();
		final IServiceClient client = new MockServiceClient();
		final DistributedObjectProxy<IDistributedStub> proxy = new DistributedObjectProxy<>(
				IDistributedStub.class,
				DEFAULT_ID,
				factory,
				client
		);
		final IDistributedStub stub = proxy.proxyInterface();

		// Act & Assert
		try {
			stub.doStuff(Stuff.EXCEPTION);
		} catch (Exception e) {
			if(e instanceof IllegalAccessException) {
				throw e;
			}
		}
	}

	@Test
	public void testGetProxy_ServerSideCorrect() throws Exception {
		// Arrange
		final IProxyFactory factory = new MockProxyFactory();
		final IServiceClient client = new MockServiceClient();
		final DistributedObjectProxy<IDistributedStub> proxy = new DistributedObjectProxy<>(
				IDistributedStub.class,
				"bar",
				factory,
				client
		);
		final IDistributedStub stub = proxy.proxyInterface();

		// Act
		IDistributedStub result = stub.doStuff(Stuff.PROXY);
		IProxy proxy2 = factory.getProxyOfObject(result);

		// Assert
		Assert.assertEquals(DEFAULT_ID, proxy2.getId());
	}

	@Test
	public void testGetProxy_ObjectMethod() throws Exception {
		// Arrange
		final IProxyFactory factory = new MockProxyFactory();
		final IServiceClient client = new MockServiceClient();
		final DistributedObjectProxy<IDistributedStub> proxy = new DistributedObjectProxy<>(
				IDistributedStub.class,
				DEFAULT_ID,
				factory,
				client
		);
		final IDistributedStub stub = proxy.proxyInterface();

		// Assert
		Assert.assertEquals(proxy.hashCode(), stub.hashCode());
		Assert.assertEquals(proxy.toString(), stub.toString());
	}

	private enum Stuff implements ITransmissible {
		EXCEPTION,
		ERROR,
		RESULT,
		PROXY
	}

	public interface IStub extends Serializable {
		IStub doStuff(Stuff i);
	}
	@DistributedVersion(of = IStub.class) public interface IDistributedStub {
		IDistributedStub doStuff(Stuff i);
	}
	private class MockServiceClient implements IServiceClient {
		@Nonnull @Override public SessionData getSessionData() {
			throw new UnsupportedOperationException();
		}
		@Nullable @Override public Serializable makeRequest(@Nonnull Serializable request, int timeout) throws InterruptedException, ServerExecutionException {
			if(request instanceof MethodRequest) {
				MethodRequest method = (MethodRequest) request;
				assert method.getArgs() != null;
				switch((Stuff)method.getArgs()[0]) {
					case EXCEPTION:
						throw new ServerExecutionException(new IllegalAccessException());
					case ERROR:
						throw new ServerExecutionException(new Error());
					case RESULT:
						return DEFAULT_ID;
					case PROXY:
						return new ProxiedObject(DEFAULT_ID, IDistributedStub.class);
				}
			}
			throw new UnsupportedOperationException();
		}
		@Nullable @Override public Serializable makeRequest(@Nonnull Serializable request) throws ServerExecutionException, InterruptedException {
			return makeRequest(request, 1000);
		}
		@Nullable @Override public <T extends Serializable> T makeRequest(@Nonnull IReturn<T> request) throws ServerExecutionException, InterruptedException {
			// noinspection unchecked
			return (T) makeRequest(request, 1000);
		}
	}
	private static class MockProxyFactory implements IProxyFactory {
		private static final Class[] PARAM_TYPES = new Class[0];
		private static final Object[] ARGS = new Object[0];
		private IProxy mProxy = null;
		@Nonnull @Override public <T> T makeRemoteProxy(@Nonnull String id, boolean overwrites, @Nonnull Class<T> distributedInterface, Serializable... args) throws Exception {
			throw new UnsupportedOperationException();
		}
		@Nonnull @Override public <T> T makeProxy(@Nonnull final String id, @Nonnull final Class<T> distributedInterface) {
			mProxy = new IProxy() {
				@Nonnull @Override public Object proxyInterface() {
					throw new UnsupportedOperationException();
				}
				@Nonnull @Override public String getId() {
					return id;
				}
				@Nonnull @Override public Class<?> getBaseInterface() {
					return distributedInterface;
				}
				@Override public Object invoke(Object o, Method method, Method method2, Object[] objects) throws Throwable {
					throw new UnsupportedOperationException();
				}
			};

			javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();
			factory.setInterfaces(new Class[] {distributedInterface});
			try {
				// noinspection unchecked
				return (T) factory.create(PARAM_TYPES, ARGS, mProxy);
			} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new AssertionError(e);
			}
		}
		@Nonnull @Override public IProxy getProxyOfObject(@Nonnull Object obj) throws NotFoundException {
			return mProxy;
		}
	}
}
