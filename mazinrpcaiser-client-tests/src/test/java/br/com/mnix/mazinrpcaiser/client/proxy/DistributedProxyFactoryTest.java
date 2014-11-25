package br.com.mnix.mazinrpcaiser.client.proxy;

import br.com.mnix.mazinrpcaiser.client.web.IServiceClient;
import br.com.mnix.mazinrpcaiser.common.DistributedVersion;
import br.com.mnix.mazinrpcaiser.common.ITransmissible;
import br.com.mnix.mazinrpcaiser.common.ProxiedObject;
import br.com.mnix.mazinrpcaiser.common.SessionData;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.common.request.CreateObjectRequest;
import br.com.mnix.mazinrpcaiser.common.request.IReturn;
import javassist.NotFoundException;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/20/14.
 *
 * @author mnix05
 */
public class DistributedProxyFactoryTest {
	private static final String DEFAULT_ID = "foo";

	@Test(expected = IllegalAccessException.class)
	public void testGetProxy_ServerSideException() throws Exception {
		// Arrange
		final IServiceClient client = new MockClient();
		final DistributedProxyFactory factory = new DistributedProxyFactory(client);

		// Act & Assert
		try {
			factory.makeRemoteProxy(DEFAULT_ID, true, IDistributedStub.class, Stuff.EXCEPTION);
		} catch (Exception e) {
			if(e instanceof IllegalAccessException) {
				throw e;
			}
		}
	}

	@Test(expected = InternalError.class)
	public void testGetProxy_ServerSideError() throws Exception {
		// Arrange
		final IServiceClient client = new MockClient();
		final DistributedProxyFactory factory = new DistributedProxyFactory(client);

		// Act & Assert
		try {
			factory.makeRemoteProxy(DEFAULT_ID, true, IDistributedStub.class, Stuff.ERROR);
		} catch (Throwable e) {
			if(e instanceof InternalError) {
				throw e;
			}
		}
	}

	@Test(expected = NotFoundException.class)
	public void testGetProxy_ProxyNotFound() throws Exception {
		// Arrange
		final IServiceClient client = new MockClient();
		final DistributedProxyFactory factory = new DistributedProxyFactory(client);
		final DistributedProxyFactory factory2 = new DistributedProxyFactory(client);

		// Act
		final Object obj = factory2.makeProxy(DEFAULT_ID, IDistributedStub.class);

		// Assert
		factory.getProxyOfObject(obj);
	}

	@Test
	public void testGetProxy_ProxyFound() throws Exception {
		// Arrange
		final IServiceClient client = new MockClient();
		final DistributedProxyFactory factory = new DistributedProxyFactory(client);

		// Act
		final Object obj = factory.makeRemoteProxy("bar", true, IDistributedStub.class, Stuff.PROXY);
		final IProxy proxy = factory.getProxyOfObject(obj);

		// Assert
		Assert.assertEquals("bar", proxy.getId());
		// noinspection unchecked
		Assert.assertTrue(proxy.getBaseInterface().isAssignableFrom(obj.getClass()));
	}

	private enum Stuff implements ITransmissible {
		EXCEPTION,
		ERROR,
		RESULT,
		PROXY
	}

	public interface IStub extends Serializable {}
	@DistributedVersion(of = IStub.class) public interface IDistributedStub extends Serializable {}

	private static class MockClient implements IServiceClient {
		@Nonnull @Override public SessionData getSessionData() {
			throw new UnsupportedOperationException();
		}
		@Nullable @Override public Serializable makeRequest(@Nonnull Serializable request, int timeout) throws InterruptedException, ServerExecutionException {
			if(request instanceof CreateObjectRequest) {
				CreateObjectRequest method = (CreateObjectRequest) request;
				assert method.getInitializationArgs() != null;
				switch((Stuff)method.getInitializationArgs()[0]) {
					case EXCEPTION:
						throw new ServerExecutionException(new IllegalAccessException());
					case ERROR:
						throw new ServerExecutionException(new InternalError());
					case RESULT:
						return DEFAULT_ID;
					case PROXY:
						return new ProxiedObject(method.getObjectId(), IDistributedStub.class);
				}
			}
			throw new UnsupportedOperationException();
		}
		@Nullable @Override public Serializable makeRequest(@Nonnull Serializable request) throws ServerExecutionException, InterruptedException {
			return makeRequest(request, 1000);
		} @Nullable @Override public <T extends Serializable> T makeRequest(@Nonnull IReturn<T> request) throws ServerExecutionException, InterruptedException {
			// noinspection unchecked
			return (T) makeRequest(request, 1000);
		}
	}
}
