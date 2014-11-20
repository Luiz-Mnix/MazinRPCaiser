package br.com.mnix.mazinrpcaiser.client;

import br.com.mnix.mazinrpcaiser.common.DistributedVersion;
import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.common.ResponseEnvelope;
import br.com.mnix.mazinrpcaiser.common.SessionData;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.common.request.MethodRequest;
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
public class DistributedObjectProxyTest {
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
		final IDistributedStub proxy = DistributedObjectProxy.forInterface(IDistributedStub.class, "foo", serviceClient);
		final Runnable processor = new Runnable() {
			@Override public void run() {
				BlockingQueue<RequestEnvelope> queue
						= HazelcastUtils.getQueue(RequestUtils.getRequestGroup(MethodRequest.class));
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
		proxy.doStuff(0);
		client.disconnect();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetProxy_ServerSideCorrect() throws Exception {
		// Arrange
		final String clusterAddress = "127.0.0.1";
		final IDataGridClient client = new DataGridClient(clusterAddress);
		final SessionData session = new SessionData("foobar", "127.0.0.1");
		final ServiceClient serviceClient = new ServiceClient(session, client);
		final IDistributedStub proxy = DistributedObjectProxy.forInterface(IDistributedStub.class, "foo", serviceClient);
		final Runnable processor = new Runnable() {
			@Override public void run() {
				BlockingQueue<RequestEnvelope> queue
						= HazelcastUtils.getQueue(RequestUtils.getRequestGroup(MethodRequest.class));
				try {
					RequestEnvelope reqEnv = queue.take();
					//noinspection ConstantConditions
					HazelcastUtils.postMessage(
							reqEnv.getTopicId(),
							new ResponseEnvelope(
									reqEnv.getTopicId(),
									reqEnv.getSessionData(),
									((MethodRequest)reqEnv.getRequest()).getArgs()[0] + "foo",
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
		Assert.assertEquals("1foo", proxy.doStuff(0));
		client.disconnect();
	}

	public interface IStub extends Serializable {
		String doStuff(int i);
	}
	@DistributedVersion(of = IStub.class) public interface IDistributedStub {
		String doStuff(int i);
	}
}
