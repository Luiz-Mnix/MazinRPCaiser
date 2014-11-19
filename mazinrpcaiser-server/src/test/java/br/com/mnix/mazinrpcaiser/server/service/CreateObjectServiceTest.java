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

import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateObjectServiceTest {
	@Test(expected = IllegalArgumentException.class)
	public void testProcessAction_BaselessService_ShouldThrowException() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(objectId, IFake.class, null);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action = new RequestEnvelope(topicId, session, data);

		// Act
		datagrid.raise();
		handler.processRequest(action, datagrid);
		datagrid.shutdown();
	}

	@Test(expected = InterfaceHasNoDefaultImplementationException.class)
	public void testProcessAction_NonDefaultImplementation_ShouldThrowException() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(objectId, IDistributedNonDefaultStub.class, null);
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
	public void testProcessAction_DefaultImplementation() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(objectId, IDistributedStub.class, null);
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
	public void testProcessAction_DefaultImplementationInvalidConstructor() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(objectId, IDistributedStub.class, "2");
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
	public void testProcessAction_DefaultImplementationPrimitiveConstructor() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(objectId, IDistributedStub.class, true);
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
	public void testProcessAction_DefaultImplementationWrapperConstructor() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(objectId, IDistributedStub.class, 1);
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
	public void testProcessAction_CorrectNonDefaultImplementation() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(
				objectId, IDistributedStub.class, NonDefaultStubStub.class
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

	@Test(expected = IllegalArgumentException.class)
	public void testProcessAction_InterfaceNonDefaultImplementation() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(
				objectId, IDistributedStub.class, IFake.class
		);
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
	public void testProcessAction_IncorrectNonDefaultImplementation() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(
				objectId, IDistributedStub.class, FakeStubStub.class
		);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action = new RequestEnvelope(topicId, session, data);

		// Act
		datagrid.raise();
		handler.processRequest(action, datagrid);
		datagrid.shutdown();
	}

	// DEFAULT IMPLEMENTATION STUB
	private interface IDefaultStub extends Serializable {}
	@DefaultImplementation	public static class DefaultStubStub implements IDefaultStub {
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
	@DistributedVersion(of = IDefaultStub.class) private interface IDistributedStub extends Serializable {}
	public static class FakeStubStub {}

	// DEFAULTLESS IMPLEMENTATION STUB
	private interface INonDefaultStub extends Serializable {}
	@DistributedVersion(of = INonDefaultStub.class) private interface IDistributedNonDefaultStub extends Serializable {}

	// Baseless Service
	private interface IFake extends Serializable {}
}