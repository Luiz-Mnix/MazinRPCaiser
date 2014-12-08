package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;
import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.common.ResponseEnvelope;
import br.com.mnix.mazinrpcaiser.common.SessionData;
import br.com.mnix.mazinrpcaiser.server.data.DataGridFactory;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import br.com.mnix.mazinrpcaiser.server.service.IService;
import br.com.mnix.mazinrpcaiser.server.service.Service;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class TaskReceiverTest {
	@SuppressWarnings("unchecked")
	@Test
	public void testSetUpReceivers() throws Throwable{
		// Arrange
		final IDataGrid grid1 = DataGridFactory.makeDefaultDataGrid();
		final IDataGrid grid2 = DataGridFactory.makeDataGrid(MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS + ":5702");
		final Field receiversField = TaskReceiver.class.getDeclaredField("sRunningReceivers");
		receiversField.setAccessible(true);
		final Map<Integer, TaskReceiver> receivers = (Map<Integer, TaskReceiver>) receiversField.get(null);

		// Act
		grid1.raise();
		grid2.raise();
		final TaskReceiver receiver1 = TaskReceiver.setUpTaskReceiver(grid1);
		final TaskReceiver receiver1Clone = TaskReceiver.setUpTaskReceiver(grid1);
		final TaskReceiver receiver2 = TaskReceiver.setUpTaskReceiver(grid2);
		final Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

		// Assert
		boolean found1 = false;
		boolean found2 = false;
		for (Thread thread : threadSet) {
			if(thread.getName().equals(receiver1.getThreadName())) {
				found1 = true;
			}
			if(thread.getName().equals(receiver2.getThreadName())) {
				found2 = true;
			}
		}
		assertTrue(found1);
		assertTrue(found2);

		assertEquals(2, receivers.size());
		assertSame(receiver1, receiver1Clone);
		assertSame(receiver1, receivers.get(grid1.hashCode()));
		assertSame(receiver2, receivers.get(grid2.hashCode()));

		receiver1.finish();
		grid2.shutdown();

		assertEquals(0, receivers.size());

		grid1.shutdown();
	}

	@Test(expected = IllegalStateException.class)
	public void testStart_AlreadyStarted() throws Throwable {
		// Arrange
		final IDataGrid grid1 = DataGridFactory.makeDefaultDataGrid();

		// Act
		grid1.raise();
		final TaskReceiver receiver1 = TaskReceiver.setUpTaskReceiver(grid1);

		// Assert
		try {
			receiver1.start();
		} finally {
			grid1.shutdown();
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testFinish_AlreadyFinish() throws Throwable {
		// Arrange
		final IDataGrid grid1 = DataGridFactory.makeDefaultDataGrid();

		// Act
		grid1.raise();
		final TaskReceiver receiver1 = TaskReceiver.setUpTaskReceiver(grid1);
		grid1.shutdown();

		// Assert
		try {
			receiver1.finish();
		} finally {
			grid1.shutdown();
		}
	}

	@Test(expected = RejectedExecutionException.class)
	public void testRun_Overflow() throws Throwable {
		// Arrange
		final IDataGrid grid1 = DataGridFactory.makeDefaultDataGrid();
		final int threads = 1;
		final String topic1 = "topic666";
		final SessionData session1 = new SessionData(topic1, MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS);
		final Throwable[] exception = new Exception[1];
		final Semaphore semaphore = new Semaphore(0);
		final RequestEnvelope req1 = new RequestEnvelope(topic1, session1, new StubRequest());
		final MessageListener<ResponseEnvelope> waiter = new MessageListener<ResponseEnvelope>() {
			@SuppressWarnings("ConstantConditions")
			@Override public void onMessage(Message message) {
				try {
					exception[0] = ((ResponseEnvelope)message.getMessageObject()).getException().getCause();
				} catch(Exception ignored) {}
				semaphore.release();
			}
		};

		// Act
		grid1.raise();
		grid1.addListener(topic1, waiter);
		TaskReceiver.setUpTaskReceiver(grid1, threads, threads);
		final BlockingQueue<RequestEnvelope> commandQueue = grid1.getCommandQueue(MazinRPCaiserConstants.COMMAND_QUEUE_ID);
		for(int idx = threads * 2; idx >= 0; --idx) {
			commandQueue.put(req1);
		}
		semaphore.acquire();
		grid1.shutdown();

		// Assert
		throw exception[0];
	}

	public static class StubRequest implements Serializable  {
		private static final long serialVersionUID = -7831208085544258658L;
	}
	@Service(forRequest = StubRequest.class) public static class StubService implements IService {
		@Nullable @Override public Serializable processRequest(@Nonnull RequestEnvelope requestEnv,
															   @Nonnull IDataGrid dataGrid) throws Throwable {
			Thread.sleep(10 * 1000);
			return null;
		}
	}
}