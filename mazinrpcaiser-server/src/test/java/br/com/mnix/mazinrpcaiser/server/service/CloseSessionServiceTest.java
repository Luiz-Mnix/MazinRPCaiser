package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.SessionData;
import br.com.mnix.mazinrpcaiser.common.request.CloseSessionRequest;
import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.server.data.DataGridFactory;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.*;

public class CloseSessionServiceTest {
	@Test
	public void testProcessAction_WipesContext() throws Exception {
		final IDataGrid dataGrid = DataGridFactory.getGrid();
		final CloseSessionService handler = new CloseSessionService();
		final String contextId = "context";
		final String topicId = "topic";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final CloseSessionRequest data1 = new CloseSessionRequest(true);
		final RequestEnvelope action1 = new RequestEnvelope(topicId, session, data1);

		dataGrid.raise();
		dataGrid.retrieveContext(contextId, false).putObject("key1", "bar");
		final Serializable processed1 = handler.processRequest(action1, dataGrid);

		assertNull(processed1);
		assertEquals(0, dataGrid.retrieveContext(contextId, false).size());

		dataGrid.shutdown();
	}

	@Test
	public void testProcessAction_DoesntWipeContext() throws Exception {
		final IDataGrid dataGrid = DataGridFactory.getGrid();
		final CloseSessionService handler = new CloseSessionService();
		final String contextId = "context3";
		final String topicId = "topic";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final CloseSessionRequest data1 = new CloseSessionRequest(false);
		final RequestEnvelope action1 = new RequestEnvelope(topicId, session, data1);

		dataGrid.raise();
		dataGrid.retrieveContext(contextId, false).putObject("key1", "bar");
		final Serializable processed1 = handler.processRequest(action1, dataGrid);

		assertNull(processed1);
		assertEquals(1, dataGrid.retrieveContext(contextId, false).size());

		dataGrid.shutdown();
	}
}