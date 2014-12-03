package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.DefaultImplementation;
import br.com.mnix.mazinrpcaiser.common.DistributedVersion;
import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.common.SessionData;
import br.com.mnix.mazinrpcaiser.common.exception.InterfaceHasNoDefaultImplementationException;
import br.com.mnix.mazinrpcaiser.common.request.CreateObjectRequest;
import br.com.mnix.mazinrpcaiser.server.data.DataGridFactory;
import br.com.mnix.mazinrpcaiser.server.data.IContext;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CreateObjectServiceTest {
	@Test(expected = InterfaceHasNoDefaultImplementationException.class)
	public void testProcessAction_NonDefaultImplementation_ShouldThrowException() throws Throwable {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.makeDefaultDataGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(objectId, true, IDistributedNonDefaultStub.class);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action = new RequestEnvelope(topicId, session, data);

		// Act
		datagrid.raise();
		handler.processRequest(action, datagrid);
		datagrid.shutdown();
	}

	@Test
	public void testProcessAction_DefaultImplementation() throws Throwable {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.makeDefaultDataGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(objectId, true, IDistributedStub.class);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action = new RequestEnvelope(topicId, session, data);

		// Act
		datagrid.raise();
		handler.processRequest(action, datagrid);

		// Assert
		final IContext context = datagrid.retrieveContext(contextId, false);
		assertTrue(context.containsObjectId(objectId));
		assertEquals(DefaultStubStub.class, context.getSerializable(objectId).getClass());

		datagrid.shutdown();
	}

	@Test(expected = NoSuchMethodException.class)
	public void testProcessAction_DefaultImplementationInvalidConstructor() throws Throwable {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.makeDefaultDataGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(objectId, true, IDistributedStub.class, "2");
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action = new RequestEnvelope(topicId, session, data);

		// Act
		datagrid.raise();
		handler.processRequest(action, datagrid);

		datagrid.shutdown();
	}

	@Test(expected = NoSuchMethodException.class)
	public void testProcessAction_DefaultImplementationPrimitiveConstructor() throws Throwable {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.makeDefaultDataGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(objectId, true, IDistributedStub.class, true);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action = new RequestEnvelope(topicId, session, data);

		// Act
		datagrid.raise();
		handler.processRequest(action, datagrid);
		datagrid.shutdown();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessAction_DefaultImplementationWrapperConstructor() throws Throwable {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.makeDefaultDataGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(objectId, true, IDistributedStub.class, 1);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action = new RequestEnvelope(topicId, session, data);

		// Act
		datagrid.raise();
		handler.processRequest(action, datagrid);
		datagrid.shutdown();
	}

	@Test
	public void testProcessAction_CorrectNonDefaultImplementation() throws Throwable {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.makeDefaultDataGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(
				objectId, true, IDistributedStub.class, NonDefaultStubStub.class
		);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action = new RequestEnvelope(topicId, session, data);

		// Act
		datagrid.raise();
		handler.processRequest(action, datagrid);
		final IContext context = datagrid.retrieveContext(contextId, false);

		// Assert
		assertTrue(context.containsObjectId(objectId));
		assertEquals(NonDefaultStubStub.class, context.getSerializable(objectId).getClass());
		datagrid.shutdown();
	}

	@SuppressWarnings("ConstantConditions")
	@Test
	public void testProcessAction_ReuseObject() throws Throwable {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.makeDefaultDataGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data1 = new CreateObjectRequest(objectId, true, IDistributedDataStub.class, "foo", "bar");
		final CreateObjectRequest data2 = new CreateObjectRequest(objectId, false, IDistributedDataStub.class, "bar", "foo");
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action1 = new RequestEnvelope(topicId, session, data1);
		final RequestEnvelope action2 = new RequestEnvelope(topicId, session, data2);

		// Act
		datagrid.raise();
		handler.processRequest(action1, datagrid);
		handler.processRequest(action2, datagrid);
		final IContext context = datagrid.retrieveContext(contextId, false);
		DataStub stub = (DataStub) context.getSerializable(objectId);

		// Assert
		assertEquals(data1.getInitializationArgs()[0], stub.getFoo());
		assertNotEquals(data2.getInitializationArgs()[0], stub.getFoo());
		datagrid.shutdown();
	}

	@SuppressWarnings("ConstantConditions")
	@Test
	public void testProcessAction_OverwriteObject() throws Throwable {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.makeDefaultDataGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data1 = new CreateObjectRequest(objectId, true, IDistributedDataStub.class, "foo", "bar");
		final CreateObjectRequest data2 = new CreateObjectRequest(objectId, true, IDistributedDataStub.class, "bar", "foo");
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action1 = new RequestEnvelope(topicId, session, data1);
		final RequestEnvelope action2 = new RequestEnvelope(topicId, session, data2);

		// Act
		datagrid.raise();
		handler.processRequest(action1, datagrid);
		handler.processRequest(action2, datagrid);
		final IContext context = datagrid.retrieveContext(contextId, false);
		DataStub stub = (DataStub) context.getSerializable(objectId);

		// Assert
		assertEquals(data2.getInitializationArgs()[0], stub.getFoo());
		assertNotEquals(data1.getInitializationArgs()[0], stub.getFoo());
		datagrid.shutdown();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessAction_FailToReuseObject() throws Throwable {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.makeDefaultDataGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data1 = new CreateObjectRequest(objectId, true, IDistributedDataStub.class, "foo", "foo");
		final CreateObjectRequest data2 = new CreateObjectRequest(objectId, false, IDistributedStub.class);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action1 = new RequestEnvelope(topicId, session, data1);
		final RequestEnvelope action2 = new RequestEnvelope(topicId, session, data2);

		// Act
		datagrid.raise();
		handler.processRequest(action1, datagrid);
		handler.processRequest(action2, datagrid);
		datagrid.shutdown();
	}

	// DEFAULT IMPLEMENTATION STUB
	private interface IDefaultStub extends Serializable {}
	@DefaultImplementation public static class DefaultStubStub implements IDefaultStub {
		private static final long serialVersionUID = -3487216679364641238L;
		public DefaultStubStub(boolean shouldCrash) {
			if(shouldCrash) {
				throw new IllegalArgumentException();
			}
		}
		public DefaultStubStub(Integer shouldCrash) {
			if(shouldCrash == 1) {
				throw new IllegalArgumentException();
			}
		}
		public DefaultStubStub() {}
	}
	public static class NonDefaultStubStub implements IDefaultStub {
		private static final long serialVersionUID = -6336135849860695589L;
	}
	@DistributedVersion(of = IDefaultStub.class) private interface IDistributedStub {}
	public static class FakeStubStub {}

	// DEFAULTLESS IMPLEMENTATION STUB
	private interface INonDefaultStub extends Serializable {}
	@DistributedVersion(of = INonDefaultStub.class) private interface IDistributedNonDefaultStub {}

	// STRANGE DATA STUB
	private interface IDataStub extends Serializable {}
	@DefaultImplementation public static class DataStub implements IDataStub {
		private static final long serialVersionUID = -3487216679364641238L;
		@Nonnull private final String mFoo;
		@Nonnull public String getFoo() {
			return mFoo;
		}
		public DataStub(@Nonnull String foo, @Nonnull String bar) {
			mFoo = foo;
		}
	}
	@DistributedVersion(of = IDataStub.class) private interface IDistributedDataStub {}
}