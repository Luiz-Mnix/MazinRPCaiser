package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;
import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.common.ResponseEnvelope;
import br.com.mnix.mazinrpcaiser.common.SessionData;
import br.com.mnix.mazinrpcaiser.common.request.MethodRequest;
import br.com.mnix.mazinrpcaiser.common.request.Request;
import br.com.mnix.mazinrpcaiser.common.request.RequestUtils;
import br.com.mnix.mazinrpcaiser.server.data.DataGridFactory;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import br.com.mnix.mazinrpcaiser.server.service.IService;
import br.com.mnix.mazinrpcaiser.server.service.RequestHasNoServiceException;
import br.com.mnix.mazinrpcaiser.server.service.Service;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.reflections.Reflections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

@SuppressWarnings({"MagicNumber", "ConstantConditions", "ThrowableResultOfMethodCallIgnored"})
public class TaskReceiverTest {

	@SuppressWarnings("deprecation")
	@After
	public void after() throws Exception {
		ThreadGroup masterGroup = Thread.currentThread().getThreadGroup();
		while(masterGroup != null && masterGroup.getParent() != null) {
			masterGroup = masterGroup.getParent();
		}
		assert masterGroup != null;

		Thread[] threads = new Thread[masterGroup.activeCount()];
		masterGroup.enumerate(threads);
		for (Thread thread : threads) {
			if(thread.getName().contains("_receiver")) {
				thread.stop();
			}
		}

		Field field = TaskReceiver.class.getDeclaredField("sRunningReceivers"); //NoSuchFieldException
		field.setAccessible(true);
		Map map = (Map) field.get(null);
		map.clear();
	}

	@Test
	public void testSetUpReceivers() throws Exception {
		// Arrange
		final IDataGrid grid = DataGridFactory.getGrid();
		final Reflections reflections = new Reflections(MazinRPCaiserConstants.DEFAULT_USER_PACKAGE);
		final Set<Class<?>> requestsClasses = reflections.getTypesAnnotatedWith(Request.class);
		final Collection<String> requestsClassesNames = Collections2.transform(requestsClasses,
				new Function<Class<?>, String>() {
					@Nullable
					@Override
					public String apply(@Nullable Class<?> aClass) {
						//noinspection unchecked,ConstantConditions
						return TaskReceiver.getReceiverName((Class<? extends Serializable>) aClass);
					}
				});

		// Act
		grid.raise();
		grid.getCommandQueue("asd");
		TaskReceiver.setUpReceivers(grid);

		ThreadGroup masterGroup = Thread.currentThread().getThreadGroup();
		while(masterGroup != null && masterGroup.getParent() != null) {
			masterGroup = masterGroup.getParent();
		}
		assert masterGroup != null;

		Set<String> requestsThreadsNames = new HashSet<>();
		Thread[] threads = new Thread[masterGroup.activeCount()];
		masterGroup.enumerate(threads);
		for (Thread thread : threads) {
			if(thread.getName().contains("_receiver")) {
				requestsThreadsNames.add(thread.getName());
			}
		}

		// Assert
		Assert.assertTrue(requestsThreadsNames.size() <= requestsClasses.size());
		Assert.assertTrue(requestsThreadsNames.size() >= 4);
		for (String requestsThreadsName : requestsThreadsNames) {
			Assert.assertTrue(requestsClassesNames.contains(requestsThreadsName));
		}

		grid.shutdown();
	}

	@Test(expected = RequestHasNoServiceException.class)
	public void testSetUpReceiver_ReceiverNotFound() throws Exception {
		// Arrange
		final IDataGrid grid = DataGridFactory.getGrid();
		final Class<? extends Serializable> requestClass = StubRequest.class;

		// Act & Assert
		grid.raise();
		TaskReceiver.setUpReceiver(requestClass, grid);

		grid.shutdown();
	}

	@Test
	public void testSetUpReceiver_ReceiverFound() throws Exception {
		// Arrange
		final IDataGrid grid = DataGridFactory.getGrid();
		final Class<? extends Serializable> requestClass = MethodRequest.class;

		// Act
		grid.raise();
		TaskReceiver.setUpReceiver(requestClass, grid);
		ThreadGroup masterGroup = Thread.currentThread().getThreadGroup();
		while(masterGroup != null && masterGroup.getParent() != null) {
			masterGroup = masterGroup.getParent();
		}

		assert masterGroup != null;
		int count = masterGroup.activeCount();
		Thread[] threads = new Thread[count];
		masterGroup.enumerate(threads);

		// Assert
		boolean found = false;
		for (Thread thread : threads) {
			if(thread.getName().equals(TaskReceiver.getReceiverName(requestClass))) {
				found = true;
				break;
			}
		}
		Assert.assertTrue(found);

		TaskReceiver.setUpReceiver(requestClass, grid);
		ThreadGroup newMasterGroup = Thread.currentThread().getThreadGroup();
		while(newMasterGroup != null && newMasterGroup.getParent() != null) {
			newMasterGroup = newMasterGroup.getParent();
		}
		assert newMasterGroup != null;
		Assert.assertEquals(count, newMasterGroup.activeCount());

		grid.shutdown();
	}

	@Test
	public void test_Receiving_ShouldReturnFoo() throws Exception {
		// Arrange
		final IDataGrid grid = DataGridFactory.getGrid();
		final String topicId = UUID.randomUUID().toString();
		final Stub2Request request1 = new Stub2Request(666);
		final SessionData session = new SessionData("", "127.0.0.1");
		final RequestEnvelope requestEnvelope = new RequestEnvelope(topicId, session, request1);
		final Semaphore semaphore = new Semaphore(0);
		final ResponseEnvelope[] responseEnvelope = new ResponseEnvelope[1];
		final MessageListener liberator = new MessageListener() {
			@Override
			public void onMessage(Message message) {
				responseEnvelope[0] = (ResponseEnvelope) message.getMessageObject();
				semaphore.release();
			}
		};

		// Act
		grid.raise();
		grid.addListener(topicId, liberator);
		TaskReceiver.setUpReceiver(Stub2Request.class, grid);
		BlockingQueue<RequestEnvelope> requestQueue = grid.getCommandQueue(RequestUtils.getRequestGroup(request1));
		requestQueue.add(requestEnvelope);
		semaphore.acquire();

		// Assert
		Assert.assertNull(responseEnvelope[0].getException());
		Assert.assertEquals("foo666", responseEnvelope[0].getResponse());
		grid.shutdown();
	}

	@Test
	public void test_Receiving_ShouldReturnException() throws Exception {
		// Arrange
		final IDataGrid grid = DataGridFactory.getGrid();
		final String topicId = UUID.randomUUID().toString();
		final Stub2Request request1 = new Stub2Request(0);
		final SessionData session = new SessionData("", "127.0.0.1");
		final RequestEnvelope requestEnvelope = new RequestEnvelope(topicId, session, request1);
		final Semaphore semaphore = new Semaphore(0);
		final ResponseEnvelope[] responseEnvelope = new ResponseEnvelope[1];
		final MessageListener liberator = new MessageListener() {
			@Override
			public void onMessage(Message message) {
				responseEnvelope[0] = (ResponseEnvelope) message.getMessageObject();
				semaphore.release();
			}
		};

		// Act
		grid.raise();
		grid.addListener(topicId, liberator);
		TaskReceiver.setUpReceiver(Stub2Request.class, grid);
		BlockingQueue<RequestEnvelope> requestQueue = grid.getCommandQueue(RequestUtils.getRequestGroup(request1));
		requestQueue.add(requestEnvelope);
		semaphore.acquire();

		// Assert
		Assert.assertNotNull(responseEnvelope[0].getException());
		Assert.assertTrue(responseEnvelope[0].getException().getCause() instanceof IllegalArgumentException);
		Assert.assertNull(responseEnvelope[0].getResponse());
		grid.shutdown();
	}

	@Request
	public static class StubRequest implements Serializable {
		private static final long serialVersionUID = -7831208085544258658L;
	}

	@Request
	public static class Stub2Request implements Serializable {
		private static final long serialVersionUID = 6171755309144019679L;
		private final int mFoo;
		public Stub2Request(int foo) {
			mFoo = foo;
		}
		public int isFoo() {
			return mFoo;
		}
	}

	@Service(forRequest = Stub2Request.class)
	public static class Stub2Service implements IService {
		@Nullable
		@Override
		public Serializable processRequest(@Nonnull RequestEnvelope requestEnv, @Nonnull IDataGrid dataGrid)
				throws Exception {
			int foo = ((Stub2Request) requestEnv.getRequest()).isFoo();
			if(foo == 0) {
				throw new IllegalArgumentException();
			}

			return "foo"+foo;
		}
	}
}