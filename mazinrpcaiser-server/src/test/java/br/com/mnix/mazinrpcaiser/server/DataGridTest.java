package br.com.mnix.mazinrpcaiser.server;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class DataGridTest {

	@Test
	public void testRetrieveContext() throws Exception {
		// Arrange
		final IDataGrid dataGrid = new DataGrid();
		final String contextId = "context";

		// Act
		dataGrid.raise();
		IContext context = dataGrid.retrieveContext(contextId, true);

		// Assert
		assertEquals(0, context.size());
		context.putObject("foo", "foo");
		assertEquals(1, dataGrid.retrieveContext(contextId, false).size());
		assertEquals(0, dataGrid.retrieveContext(contextId, true).size());

		dataGrid.shutdown();
	}

	@Test
	public void testDeleteContext() throws Exception {
		// Arrange
		final IDataGrid dataGrid = new DataGrid();
		final String contextId = "context2";

		// Act
		dataGrid.raise();
		IContext context = dataGrid.retrieveContext(contextId, true);
		context.putObject("foo", "foo");

		// Assert
		assertEquals(1, dataGrid.retrieveContext(contextId, false).size());
		dataGrid.deleteContext(contextId);
		assertEquals(0, dataGrid.retrieveContext(contextId, false).size());

		dataGrid.shutdown();
	}

	@Test
	public void testGetCommandQueue() throws Exception {
		// Arrange
		final IDataGrid dataGrid = new DataGrid();
		final String queueId = "queue";
		final Runnable putter = new Runnable() {
			@Override
			public void run() {
				BlockingQueue<String> queue = dataGrid.getCommandQueue(queueId);
				try {
					Thread.sleep(1000);
					queue.put("foo");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		// Act
		dataGrid.raise();
		new Thread(putter).start();
		final BlockingQueue<String> queue = dataGrid.getCommandQueue(queueId);
		String command = queue.poll(2, TimeUnit.SECONDS);

		// Assert

		assertEquals("foo", command);
		dataGrid.shutdown();
	}

	@Test
	public void testPostNotification() throws Exception {
		// Arrange
		final IDataGrid dataGrid = new DataGrid();
		final String topicId = "topic";
		final Semaphore semaphore = new Semaphore(0);
		final String[] data = new String[1];
		final MessageListener listener = new MessageListener() {
			@Override
			public void onMessage(Message message) {
				data[0] = message.getMessageObject().toString();
				semaphore.release();
			}
		};
		final TimerTask timeout = new TimerTask() {
			@Override
			public void run() {
				semaphore.release();
			}
		};

		// Act
		dataGrid.raise();
		final String listenerId = dataGrid.addListener(topicId, listener);
		dataGrid.postNotification(topicId, "foo");
		semaphore.acquire();

		// Assert
		assertEquals("foo", data[0]);
		dataGrid.removeListener(topicId, listenerId);
		new Timer().schedule(timeout, 1000);
		dataGrid.postNotification(topicId, "bar");
		semaphore.acquire();
		assertEquals("foo", data[0]);

		dataGrid.shutdown();
	}
}