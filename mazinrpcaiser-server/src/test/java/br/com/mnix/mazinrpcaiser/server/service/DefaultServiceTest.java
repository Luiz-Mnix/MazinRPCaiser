package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.*;
import br.com.mnix.mazinrpcaiser.common.request.CloseSessionRequest;
import br.com.mnix.mazinrpcaiser.server.data.DataGridFactory;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import org.junit.Test;

import java.io.Serializable;

public class DefaultServiceTest {
	@Test(expected = IllegalArgumentException.class)
	public void testProcessAction_wrongData() throws Throwable {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.makeDefaultDataGrid();
		final DefaultService handler = new OpenSessionService();
		final Serializable data = new CloseSessionRequest(true);
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionData session = new SessionData(contextId, "127.0.0.1");
		final RequestEnvelope action = new RequestEnvelope(topicId, session, data);

		//Act
		datagrid.raise();
		handler.processRequest(action, datagrid);
	}
}