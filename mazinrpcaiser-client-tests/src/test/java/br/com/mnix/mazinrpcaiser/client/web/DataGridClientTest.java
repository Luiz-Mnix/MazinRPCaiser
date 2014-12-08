package br.com.mnix.mazinrpcaiser.client.web;
import br.com.mnix.mazinrpcaiser.client.HazelcastUtils;
import br.com.mnix.mazinrpcaiser.client.web.DataGridClient;
import br.com.mnix.mazinrpcaiser.client.web.DataGridUnavailableException;
import br.com.mnix.mazinrpcaiser.client.web.IDataGridClient;
import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import org.junit.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;

/**
 * Created by mnix05 on 11/4/14.
 *
 * @author mnix05
 */
@SuppressWarnings("MagicNumber")
public class DataGridClientTest implements MessageListener {
	@BeforeClass
	public static void Setup() {
		HazelcastUtils.raiseHazelcast();
	}

	@AfterClass
	public static void Shutdown() {
		HazelcastUtils.shutdownHazelcast();
	}

	@Test
	public void testConnect_ValidEndpoint_ShouldConnect() throws Exception{
		// Arrange
		final String clusterAddress = "127.0.0.1";
		final IDataGridClient client = new DataGridClient(clusterAddress);

		// Act
		client.connect();

		// Assert
		assertTrue(client.isConnected());
		client.disconnect();
		assertFalse(client.isConnected());
	}

	@Test(expected = DataGridUnavailableException.class)
	public void testConnect_InvalidEndpoint_ShouldThrowException() throws Exception{
		// Arrange
		final String clusterAddress = "1272.0.0.1";
		final IDataGridClient client = new DataGridClient(clusterAddress);

		// Act & Assert
		client.connect();
	}

	@Test
	public void testSendData_ShouldRespondAndFree() throws Exception {
		// Arrange
		final String clusterAddress = "127.0.0.1";
		final IDataGridClient client = new DataGridClient(clusterAddress);
		final Semaphore semaphore = new Semaphore(0);
		final String queueId = MazinRPCaiserConstants.COMMAND_QUEUE_ID;
		final String[] data = new String[1];
		final Runnable responder = new Runnable() {
			@Override
			public void run() {
				try {
					BlockingQueue<String> a = HazelcastUtils.getQueue(queueId);
					data[0] = a.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				semaphore.release();
			}
		};

		// Act
		client.connect();
		new Thread(responder).start();
		client.sendData("foo");
		semaphore.acquire();

		// Assert
		assertEquals("foo", data[0]);
	}

	private final Semaphore mListenerSemaphore = new Semaphore(0);

	@Test
	public void testAddListener_ShouldRespondAndFree() throws Exception {
		// Arrange
		final String clusterAddress = "127.0.0.1";
		final IDataGridClient client = new DataGridClient(clusterAddress);
		final String topicId = "topic";
		final String[] data = new String[1];
		final Runnable responder = new Runnable() {
			@Override
			public void run() {
				data[0] = "bar";
				HazelcastUtils.postMessage(topicId, data[0]);
			}
		};

		// Act
		client.connect();
		final String listenerId = client.addListener(topicId, this);
		new Thread(responder).start();
		mListenerSemaphore.acquire();
		HazelcastUtils.removeListener(topicId, listenerId);

		// Assert
		assertEquals("bar", data[0]);
		Timer timer = new Timer(topicId);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				data[0] = "foo";
				mListenerSemaphore.release();
			}
		}, 3000);
		new Thread(responder).start();
		mListenerSemaphore.acquire();
		assertEquals("foo", data[0]);
	}

	@Override
	public void onMessage(Message message) {
		mListenerSemaphore.release();
	}
}
