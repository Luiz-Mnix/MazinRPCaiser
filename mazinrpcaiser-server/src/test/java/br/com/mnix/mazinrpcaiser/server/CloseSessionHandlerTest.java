package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.CloseSessionData;
import br.com.mnix.mazinrpcaiser.common.InputAction;
import br.com.mnix.mazinrpcaiser.common.SessionMetadata;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.*;

public class CloseSessionHandlerTest {
	@Test
	public void testProcessAction() throws Exception {
		final IDataGrid dataGrid = DataGridFactory.getGrid();
		final CloseSessionHandler handler = new CloseSessionHandler();
		final String contextId = "context";
		final String topicId = "topic";
		final SessionMetadata session = new SessionMetadata(contextId, "127.0.0.1");
		final CloseSessionData data1 = new CloseSessionData();
		final InputAction action1 = new InputAction(topicId, session, data1);

		dataGrid.raise();
		dataGrid.retrieveContext(contextId, false).putObject("key1", "bar");
		final Serializable processed1 = handler.processAction(action1, dataGrid);

		assertNull(processed1);
		assertEquals(0, dataGrid.retrieveContext(contextId, false).size());

		dataGrid.shutdown();
	}
}