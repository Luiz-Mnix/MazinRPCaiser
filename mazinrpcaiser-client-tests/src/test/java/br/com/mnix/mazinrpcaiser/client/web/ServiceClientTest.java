package br.com.mnix.mazinrpcaiser.client.web;

import br.com.mnix.mazinrpcaiser.client.HazelcastUtils;
import br.com.mnix.mazinrpcaiser.client.web.DataGridClient;
import br.com.mnix.mazinrpcaiser.client.web.IDataGridClient;
import br.com.mnix.mazinrpcaiser.client.web.ServiceClient;
import br.com.mnix.mazinrpcaiser.common.*;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.common.request.IReturn;
import br.com.mnix.mazinrpcaiser.common.request.RequestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

/**
 * Created by mnix05 on 11/4/14.
 *
 * @author mnix05
 */
public class ServiceClientTest {
	@Before
	public void setup() {
		HazelcastUtils.raiseHazelcast();
	}

	@After
	public void shutdown() {
		HazelcastUtils.shutdownHazelcast();
	}

	@Test
	public void testRequestData_idealScenario_ShouldWorkJustFine() throws Exception {
		// Arrange
		final String clusterAddress = "127.0.0.1";
		final IDataGridClient client = new DataGridClient(clusterAddress);
		final SessionData session = new SessionData("foobar", "127.0.0.1");
		final ServiceClient serviceClient = new ServiceClient(session, client);
		final Runnable responder = new Runnable() {
			@Override
			public void run() {
				try {
					BlockingQueue<RequestEnvelope> queue =
							HazelcastUtils.getQueue(RequestUtils.getRequestGroup(StubActionData.class));
					RequestEnvelope action = queue.take();
					HazelcastUtils.postMessage(
							action.getTopicId(),
							new ResponseEnvelope(
									action.getTopicId(),
									session,
									((StubActionData)action.getRequest()).getFoo() + "_foo",
									null
							)
					);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		// Act
		client.connect();
		new Thread(responder).start();
		final String returned = serviceClient.makeRequest(new StubActionData(2));

		// Assert
		assertEquals("2_foo", returned);
	}

	@Test(expected = TimeoutException.class)
	public void testRequestData_TakingTooLong_ShouldTimeOut() throws Throwable{
		// Arrange
		final String clusterAddress = "127.0.0.1";
		final IDataGridClient client = new DataGridClient(clusterAddress);
		final SessionData session = new SessionData("foobar", "127.0.0.1");
		final ServiceClient serviceClient = new ServiceClient(session, client);

		// Act
		client.connect();
		try {
			serviceClient.makeRequest(new StubActionData(2));
		} catch (ServerExecutionException e) {
			throw e.getCause();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static class StubActionData implements IReturn<String> {
		private static final long serialVersionUID = 1650940652510382667L;
		@Nonnull private final Integer mFoo;
		@Nonnull public Integer getFoo() {
		    return mFoo;
		}

		public StubActionData(@Nonnull Integer foo) {
			mFoo = foo;
		}
	}
}
