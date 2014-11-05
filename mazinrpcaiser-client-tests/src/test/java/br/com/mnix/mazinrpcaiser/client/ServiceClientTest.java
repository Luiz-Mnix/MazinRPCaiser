package br.com.mnix.mazinrpcaiser.client;

import br.com.mnix.mazinrpcaiser.common.*;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
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
		HazelcastUtils.shutdowHazelcast();
	}

	@Test
	public void testRequestData_idealScenario_ShouldWorkJustFine() throws Exception {
		// Arrange
		final String clusterAddress = "127.0.0.1";
		final IDataGridClient client = new DataGridClient(clusterAddress);
		final ServiceClient serviceClient = new ServiceClient(client);
		final SessionMetadata session = new SessionMetadata("foobar", "127.0.0.1");
		final Runnable responder = new Runnable() {
			@Override
			public void run() {
				try {
					BlockingQueue<InputAction> queue =
							HazelcastUtils.getQueue(ActionDataUtils.getActionType(StubActionData.class));
					InputAction action = queue.take();
					HazelcastUtils.postMessage(
							action.getTopicId(),
							new OutputAction(
									action.getTopicId(),
									session,
									((StubActionData)action.getActionData()).getFoo() + "_foo",
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
		final String returned = serviceClient.requestAction(new StubActionData(2), session);

		// Assert
		assertEquals("2_foo", returned);
	}

	@Test(expected = TimeoutException.class)
	public void testRequestData_TakingTooLong_ShouldTimeOut() throws Throwable{
		// Arrange
		final String clusterAddress = "127.0.0.1";
		final IDataGridClient client = new DataGridClient(clusterAddress);
		final ServiceClient serviceClient = new ServiceClient(client);
		final SessionMetadata session = new SessionMetadata("foobar", "127.0.0.1");

		// Act
		client.connect();
		try {
			serviceClient.requestAction(new StubActionData(2), session);
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
