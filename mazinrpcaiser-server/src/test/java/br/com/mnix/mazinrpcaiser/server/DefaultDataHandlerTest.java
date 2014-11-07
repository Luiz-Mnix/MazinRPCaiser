package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.*;
import org.junit.Test;

public class DefaultDataHandlerTest {
	@Test(expected = IllegalArgumentException.class)
	public void testProcessAction_wrongData() throws Exception {
		// Arrange
		final IDataGrid datagrid = DataGridFactory.getGrid();
		final DefaultDataHandler handler = new OpenSessionDataHandler();
		final IActionData data = new CloseSessionData();
		final String topicId = "topic";
		final String contextId = "context1";
		final SessionMetadata session = new SessionMetadata(contextId, "127.0.0.1");
		final InputAction action = new InputAction(topicId, session, data);

		//Act
		datagrid.raise();
		handler.processAction(action, datagrid);
	}
}