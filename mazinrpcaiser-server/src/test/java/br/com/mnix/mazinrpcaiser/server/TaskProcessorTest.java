package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;
import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.common.ResponseEnvelope;
import br.com.mnix.mazinrpcaiser.common.SessionData;
import br.com.mnix.mazinrpcaiser.server.data.IContext;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import br.com.mnix.mazinrpcaiser.server.service.IService;
import br.com.mnix.mazinrpcaiser.server.service.RequestHasNoServiceException;
import br.com.mnix.mazinrpcaiser.server.service.Service;
import com.hazelcast.core.MessageListener;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.*;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class TaskProcessorTest {
	@Test
	public void testRun() throws Exception {
		// Arrange
		final String topicId = "topic";
		final SessionData session = new SessionData(topicId, MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS);
		final RequestEnvelope requestEnv = new RequestEnvelope(topicId, session, new StubRequest(false));
		final MockGrid grid = new MockGrid();
		final TaskProcessor processor = new TaskProcessor(requestEnv, grid);

		// Act
		processor.run();
		final Object data = grid.mNotificationData;

		// Assert
		assertTrue(data instanceof ResponseEnvelope);
		final ResponseEnvelope response = (ResponseEnvelope)data;
		assertSame(session, response.getSessionData());
		assertEquals(topicId, response.getTopicId());
		assertSame(requestEnv.getRequest(), response.getResponse());
		assertNull(response.getException());
	}

	@Test(expected = IllegalStateException.class)
	public void testRun_RunningException() throws Throwable {
		// Arrange
		final String topicId = "topic";
		final SessionData session = new SessionData(topicId, MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS);
		final RequestEnvelope requestEnv = new RequestEnvelope(topicId, session, new StubRequest(true));
		final MockGrid grid = new MockGrid();
		final TaskProcessor processor = new TaskProcessor(requestEnv, grid);

		// Act
		processor.run();
		final Object data = grid.mNotificationData;

		// Assert
		assertTrue(data instanceof ResponseEnvelope);
		final ResponseEnvelope response = (ResponseEnvelope)data;
		assertSame(session, response.getSessionData());
		assertEquals(topicId, response.getTopicId());
		assertNull(response.getResponse());
		assertNotNull(response.getException());
		assertTrue(response.getException().getCause() instanceof IllegalStateException);

		throw response.getException().getCause();
	}

	@Test(expected = RequestHasNoServiceException.class)
	public void testRun_Exception() throws Throwable {
		// Arrange
		final String topicId = "topic";
		final SessionData session = new SessionData(topicId, MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS);
		final RequestEnvelope requestEnv = new RequestEnvelope(topicId, session, new Serializable() {
			private static final long serialVersionUID = 3673377533388099430L;
		});
		final MockGrid grid = new MockGrid();
		final TaskProcessor processor = new TaskProcessor(requestEnv, grid);

		// Act
		processor.run();
		final Object data = grid.mNotificationData;

		// Assert
		assertTrue(data instanceof ResponseEnvelope);
		final ResponseEnvelope response = (ResponseEnvelope)data;
		assertSame(session, response.getSessionData());
		assertEquals(topicId, response.getTopicId());
		assertNull(response.getResponse());
		assertNotNull(response.getException());
		assertTrue(response.getException().getCause() instanceof RequestHasNoServiceException);

		throw response.getException().getCause();
	}

	public static class StubRequest implements Serializable  {
		private static final long serialVersionUID = -7831208085544258658L;
		private final boolean mCrash;
		public boolean getCrash() {
			return mCrash;
		}
		public StubRequest(boolean crash) {
			mCrash = crash;
		}
	}
	@Service(forRequest = StubRequest.class) public static class StubService implements IService {
		@Nullable @Override public Serializable processRequest(@Nonnull RequestEnvelope requestEnv,
															   @Nonnull IDataGrid dataGrid) throws Throwable {
			if(((StubRequest)requestEnv.getRequest()).getCrash()) {
				throw new IllegalStateException();
			}
			return requestEnv.getRequest();
		}
	}

	private class MockGrid implements IDataGrid {
		public Object mNotificationData = null;
		@Override public boolean isOn() {
			return true;
		}
		@Override public void raise() {}
		@Override public void shutdown() {}
		@Nonnull @Override public <T> BlockingQueue<T> getCommandQueue(@Nonnull String commandName) {
			throw new UnsupportedOperationException();
		}
		@Nonnull @Override public IContext retrieveContext(@Nonnull String contextId, boolean overwrites) {
			throw new UnsupportedOperationException();
		}
		@Override public void deleteContext(@Nonnull String contextId) {}
		@Override public <T extends Serializable> void postNotification(String topicId, T data) {
			mNotificationData = data;
		}
		@Nonnull @Override public <T> String addListener(@Nonnull String topicId, @Nonnull MessageListener<T> listener) {
			throw new UnsupportedOperationException();
		}
		@Override public void removeListener(@Nonnull String topicId, @Nonnull String listenerId) {}
	}
}