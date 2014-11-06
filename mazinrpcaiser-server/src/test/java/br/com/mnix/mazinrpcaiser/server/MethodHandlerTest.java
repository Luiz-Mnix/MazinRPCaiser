package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.*;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.*;

public class MethodHandlerTest {

	@Test
	public void testProcessAction_CorrectMethod() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final MethodHandler handler = new MethodHandler();
		final String objectId = "obj";
		final MethodData data = new MethodData(objectId, "foo", new Serializable[] { 1 });
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionMetadata session = new SessionMetadata(contextId, "127.0.0.1");
		final InputAction action = new InputAction(topicId, session, data);
		final DefaultStubStub obj = new DefaultStubStub();

		// Act
		datagrid.raise();
		datagrid.retrieveContext(contextId, true).putObject(objectId, obj);
		final String returnedData = (String) handler.processAction(action, datagrid);

		// Assert
		assertEquals("3_foo", returnedData);

		datagrid.shutdown();
	}

	@Test(expected = NotImplementedException.class)
	public void testProcessAction_MethodException() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final MethodHandler handler = new MethodHandler();
		final String objectId = "obj";
		final MethodData data = new MethodData(objectId, "throwException", null);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionMetadata session = new SessionMetadata(contextId, "127.0.0.1");
		final InputAction action = new InputAction(topicId, session, data);
		final DefaultStubStub obj = new DefaultStubStub();

		// Act
		datagrid.raise();
		datagrid.retrieveContext(contextId, true).putObject(objectId, obj);
		handler.processAction(action, datagrid);
		datagrid.shutdown();
	}

	@Test(expected = NoSuchMethodException.class)
	public void testProcessAction_PrimitiveArgs() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final MethodHandler handler = new MethodHandler();
		final String objectId = "obj";
		final MethodData data = new MethodData(objectId, "primitiveArgMethod", new Serializable[] { 1 });
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionMetadata session = new SessionMetadata(contextId, "127.0.0.1");
		final InputAction action = new InputAction(topicId, session, data);
		final DefaultStubStub obj = new DefaultStubStub();

		// Act
		datagrid.raise();
		datagrid.retrieveContext(contextId, true).putObject(objectId, obj);
		handler.processAction(action, datagrid);
	}

	@Test
	public void testProcessAction_WrapperArgs_PrimitiveReturn() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final MethodHandler handler = new MethodHandler();
		final String objectId = "obj";
		final float value = 1.f;
		final MethodData data = new MethodData(objectId, "wrapperArgMethod", new Serializable[] { value });
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionMetadata session = new SessionMetadata(contextId, "127.0.0.1");
		final InputAction action = new InputAction(topicId, session, data);
		final DefaultStubStub obj = new DefaultStubStub();

		// Act
		datagrid.raise();
		datagrid.retrieveContext(contextId, true).putObject(objectId, obj);
		final float returnedData = (float) handler.processAction(action, datagrid);

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
			throw new NotImplementedException("FOOCK");
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