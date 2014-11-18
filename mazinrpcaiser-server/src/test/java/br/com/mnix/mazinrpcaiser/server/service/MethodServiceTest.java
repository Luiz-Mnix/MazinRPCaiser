package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.*;
import br.com.mnix.mazinrpcaiser.common.request.MethodRequest;
import br.com.mnix.mazinrpcaiser.server.data.DataGridFactory;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.*;

public class MethodServiceTest {

	@Test
	public void testProcessAction_CorrectMethod() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final MethodService handler = new MethodService();
		final String objectId = "obj";
		final MethodRequest data = new MethodRequest(objectId, "foo", 1);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action = new RequestEnvelope(topicId, session, data);
		final DefaultStubStub obj = new DefaultStubStub();

		// Act
		datagrid.raise();
		datagrid.retrieveContext(contextId, true).putObject(objectId, obj);
		final String returnedData = (String) handler.processRequest(action, datagrid);

		// Assert
		assertEquals("3_foo", returnedData);

		datagrid.shutdown();
	}

	@Test(expected = NotImplementedException.class)
	public void testProcessAction_MethodException() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final MethodService handler = new MethodService();
		final String objectId = "obj";
		final MethodRequest data = new MethodRequest(objectId, "throwException", null);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action = new RequestEnvelope(topicId, session, data);
		final DefaultStubStub obj = new DefaultStubStub();

		// Act
		datagrid.raise();
		datagrid.retrieveContext(contextId, true).putObject(objectId, obj);
		handler.processRequest(action, datagrid);
		datagrid.shutdown();
	}

	@Test(expected = NoSuchMethodException.class)
	public void testProcessAction_PrimitiveArgs() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final MethodService handler = new MethodService();
		final String objectId = "obj";
		final MethodRequest data = new MethodRequest(objectId, "primitiveArgMethod", 1);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action = new RequestEnvelope(topicId, session, data);
		final DefaultStubStub obj = new DefaultStubStub();

		// Act
		datagrid.raise();
		datagrid.retrieveContext(contextId, true).putObject(objectId, obj);
		handler.processRequest(action, datagrid);
	}

	@Test
	public void testProcessAction_WrapperArgs_PrimitiveReturn() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final MethodService handler = new MethodService();
		final String objectId = "obj";
		final float value = 1.f;
		final MethodRequest data = new MethodRequest(objectId, "wrapperArgMethod", value);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action = new RequestEnvelope(topicId, session, data);
		final DefaultStubStub obj = new DefaultStubStub();

		// Act
		datagrid.raise();
		datagrid.retrieveContext(contextId, true).putObject(objectId, obj);
		final float returnedData = (float) handler.processRequest(action, datagrid);

		// Assert
		assertEquals(value, returnedData, 0);

		datagrid.shutdown();
	}

	// DEFAULT IMPLEMENTATION STUB
	private interface IDefaultStub extends Serializable {
		String foo(Integer bar);
		void throwException() throws NotImplementedException;
		void primitiveArgMethod(int i);
		float wrapperArgMethod(Float a);
	}
	@DefaultImplementation
	public static class DefaultStubStub implements IDefaultStub {
		private static final long serialVersionUID = -3487216679364641238L;

		@Override
		public String foo(Integer bar) {
			return (bar * 3) + "_foo";
		}

		@Override
		public void throwException() throws NotImplementedException {
			throw new NotImplementedException("test");
		}

		@Override
		public void primitiveArgMethod(int i) {
		}

		@Override
		public float wrapperArgMethod(Float a) {
			return a;
		}
	}
}