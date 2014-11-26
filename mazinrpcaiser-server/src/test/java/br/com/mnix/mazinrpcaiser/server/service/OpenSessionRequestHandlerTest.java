package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.common.SessionData;
import br.com.mnix.mazinrpcaiser.common.request.OpenSessionRequest;
import br.com.mnix.mazinrpcaiser.server.data.DataGridFactory;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.*;

public class OpenSessionRequestHandlerTest {
	@Test
	public void testProcessAction() throws Throwable {
		final IDataGrid dataGrid = DataGridFactory.getGrid();
		final OpenSessionService handler = new OpenSessionService();
		final String contextId = "context";
		final String topicId = "topic";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final OpenSessionRequest data1 = new OpenSessionRequest(false);
		final RequestEnvelope action1 = new RequestEnvelope(topicId, session, data1);
		final OpenSessionRequest data2 = new OpenSessionRequest(true);
		final RequestEnvelope action2 = new RequestEnvelope(topicId, session, data2);

		dataGrid.raise();
		final Serializable processed1 = handler.processRequest(action1, dataGrid);
		dataGrid.retrieveContext(contextId, false).putObject("key1", "bar");
		final Serializable processed2 = handler.processRequest(action2, dataGrid);

		assertNull(processed1);
		assertNull(processed2);
		assertEquals(0, dataGrid.retrieveContext(contextId, false).size());

		dataGrid.shutdown();
	}
}