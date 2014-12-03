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
	public void testProcessAction_CorrectMethod() throws Throwable {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.makeDefaultDataGrid();
		final MethodService handler = new MethodService();
		final String objectId = "obj";
		final MethodRequest data = new MethodRequest(objectId, "foo", 1);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS);
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
	public void testProcessAction_MethodException() throws Throwable {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.makeDefaultDataGrid();
		final MethodService handler = new MethodService();
		final String objectId = "obj";
		final MethodRequest data = new MethodRequest(objectId, "throwException");
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS);
		final RequestEnvelope action = new RequestEnvelope(topicId, session, data);
		final DefaultStubStub obj = new DefaultStubStub();

		// Act
		datagrid.raise();
		datagrid.retrieveContext(contextId, true).putObject(objectId, obj);
		handler.processRequest(action, datagrid);
		datagrid.shutdown();
	}

	@Test(expected = NoSuchMethodException.class)
	public void testProcessAction_PrimitiveArgs() throws Throwable {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.makeDefaultDataGrid();
		final MethodService handler = new MethodService();
		final String objectId = "obj";
		final MethodRequest data = new MethodRequest(objectId, "primitiveArgMethod", 1);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS);
		final RequestEnvelope action = new RequestEnvelope(topicId, session, data);
		final DefaultStubStub obj = new DefaultStubStub();

		// Act
		datagrid.raise();
		datagrid.retrieveContext(contextId, true).putObject(objectId, obj);
		handler.processRequest(action, datagrid);
	}
	
	@Test
	public void testProcessAction_KeepState_WrapperArgs_PrimitiveReturn() throws Throwable {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.makeDefaultDataGrid();
		final MethodService handler = new MethodService();
		final String objectId = "obj";
		final int value = 1;
		final MethodRequest requestSet = new MethodRequest(objectId, "foo", value);
		final MethodRequest requestGet = new MethodRequest(objectId, "getFoo");
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS);
		final RequestEnvelope reqEnvSet = new RequestEnvelope(topicId, session, requestSet);
		final RequestEnvelope reqEnvGet = new RequestEnvelope(topicId, session, requestGet);
		final DefaultStubStub obj = new DefaultStubStub();

		// Act
		datagrid.raise();
		datagrid.retrieveContext(contextId, true).putObject(objectId, obj);
		handler.processRequest(reqEnvSet, datagrid);
		final int returnedData = (int) handler.processRequest(reqEnvGet, datagrid);

		// Assert
		assertEquals(value, returnedData);

		datagrid.shutdown();
	}

	// DEFAULT IMPLEMENTATION STUB
	private interface IDefaultStub extends Serializable {
		String foo(Integer bar);
		int getFoo();
		void throwException() throws NotImplementedException;
		void primitiveArgMethod(int i);
		float wrapperArgMethod(Float a);
	}
	@DefaultImplementation
	public static class DefaultStubStub implements IDefaultStub {
		private static final long serialVersionUID = -3487216679364641238L;
		
		private int mBar = 0;

		@Override
		public String foo(Integer bar) {
			mBar = bar;
			return (bar * 3) + "_foo";
		}

		@Override
		public int getFoo() {
			return mBar;
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