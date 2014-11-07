package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.*;
import br.com.mnix.mazinrpcaiser.common.exception.InterfaceDoesNotExistException;
import br.com.mnix.mazinrpcaiser.common.exception.InterfaceHasNoDefaultImplementationException;
import br.com.mnix.mazinrpcaiser.common.request.CreateObjectRequest;
import br.com.mnix.mazinrpcaiser.server.data.DataGridFactory;
import br.com.mnix.mazinrpcaiser.server.data.IContext;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.Serializable;

public class CreateObjectServiceTest {
	@Test(expected = InterfaceDoesNotExistException.class)
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
		assertTrue(context.containsObject(objectId));
		assertEquals(DefaultStubStub.class, context.getSerializable(objectId).getClass());

		datagrid.shutdown();
	}

	@Test(expected = NoSuchMethodException.class)
	public void testProcessAction_DefaultImplementationInvalidConstructor() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final CreateObjectService handler = new CreateObjectService();
		final String objectId = "obj";
		final CreateObjectRequest data = new CreateObjectRequest(objectId, IDistributedStub.class, new Serializable[] { "2" });
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
		final CreateObjectRequest data = new CreateObjectRequest(objectId, IDistributedStub.class, new Serializable[] { true });
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
		final CreateObjectRequest data = new CreateObjectRequest(objectId, IDistributedStub.class, new Serializable[] { 1 });
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
	@DistributedVersion(of = IDefaultStub.class) private interface IDistributedStub extends Serializable {}

	// DEFAULTLESS IMPLEMENTATION STUB
	private interface INonDefaultStub extends Serializable {}
	@DistributedVersion(of = INonDefaultStub.class) private interface IDistributedNonDefaultStub extends Serializable {}

	// Baseless Service
	private interface IFake extends Serializable {}
}