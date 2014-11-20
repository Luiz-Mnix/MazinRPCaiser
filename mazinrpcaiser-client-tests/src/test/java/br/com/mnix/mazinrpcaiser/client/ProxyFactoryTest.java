package br.com.mnix.mazinrpcaiser.client;

import br.com.mnix.mazinrpcaiser.common.DistributedVersion;
import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.common.ResponseEnvelope;
import br.com.mnix.mazinrpcaiser.common.SessionData;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.common.request.CreateObjectRequest;
import br.com.mnix.mazinrpcaiser.common.request.RequestUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

/**
 * Created by mnix05 on 11/20/14.
 *
 * @author mnix05
 */
public class ProxyFactoryTest {
	@Before
	public void setup() {
		HazelcastUtils.raiseHazelcast();
	}

	@After
	public void shutdown() {
		HazelcastUtils.shutdownHazelcast();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetProxy_ServerSideException() throws Exception {
		// Arrange
		final String clusterAddress = "127.0.0.1";
		final IDataGridClient client = new DataGridClient(clusterAddress);
		final SessionData session = new SessionData("foobar", "127.0.0.1");
		final ServiceClient serviceClient = new ServiceClient(session, client);
		final ProxyFactory factory = new ProxyFactory(serviceClient);
		final Runnable processor = new Runnable() {
			@Override public void run() {
				BlockingQueue<RequestEnvelope> queue
						= HazelcastUtils.getQueue(RequestUtils.getRequestGroup(CreateObjectRequest.class));
				try {
					RequestEnvelope reqEnv = queue.take();
					HazelcastUtils.postMessage(
							reqEnv.getTopicId(),
							new ResponseEnvelope(
									reqEnv.getTopicId(),
									reqEnv.getSessionData(),
									null,
									new ServerExecutionException(new UnsupportedOperationException())
							)
					);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		};

		// Act & Assert
		client.connect();
		new Thread(processor).start();
		factory.getProxy(IDistributedStub.class);
	}

	@Test(expected = AssertionError.class)
	public void testGetProxy_ServerSideError() throws Exception {
		// Arrange
		final String clusterAddress = "127.0.0.1";
		final IDataGridClient client = new DataGridClient(clusterAddress);
		final SessionData session = new SessionData("foobar", "127.0.0.1");
		final ServiceClient serviceClient = new ServiceClient(session, client);
		final ProxyFactory factory = new ProxyFactory(serviceClient);
		final Runnable processor = new Runnable() {
			@Override public void run() {
				BlockingQueue<RequestEnvelope> queue
						= HazelcastUtils.getQueue(RequestUtils.getRequestGroup(CreateObjectRequest.class));
				try {
					RequestEnvelope reqEnv = queue.take();
					HazelcastUtils.postMessage(
							reqEnv.getTopicId(),
							new ResponseEnvelope(
									reqEnv.getTopicId(),
									reqEnv.getSessionData(),
									null,
									new ServerExecutionException(new AssertionError())
							)
					);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		};

		// Act & Assert
		client.connect();
		new Thread(processor).start();
		factory.getProxy(IDistributedStub.class);
	}

	@Test
	public void testGetProxy_ServerSuccessful() throws Exception {
		// Arrange
		final String clusterAddress = "127.0.0.1";
		final IDataGridClient client = new DataGridClient(clusterAddress);
		final SessionData session = new SessionData("foobar", "127.0.0.1");
		final ServiceClient serviceClient = new ServiceClient(session, client);
		final ProxyFactory factory = new ProxyFactory(serviceClient);
		final Runnable processor = new Runnable() {
			@Override public void run() {
				BlockingQueue<RequestEnvelope> queue
						= HazelcastUtils.getQueue(RequestUtils.getRequestGroup(CreateObjectRequest.class));
				try {
					RequestEnvelope reqEnv = queue.take();
					HazelcastUtils.postMessage(
							reqEnv.getTopicId(),
							new ResponseEnvelope(
									reqEnv.getTopicId(),
									reqEnv.getSessionData(),
									null,
									null
							)
					);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		};

		// Act
		client.connect();
		new Thread(processor).start();
		Object proxy = factory.getProxy(IDistributedStub.class);

		// Assert
		Assert.assertNotEquals(IDistributedStub.class, proxy.getClass());
		Assert.assertTrue(proxy.getClass().getName().contains(IDistributedStub.class.getName()));
	}

	public interface IStub extends Serializable {}
	@DistributedVersion(of = IStub.class) public interface IDistributedStub {}
}
