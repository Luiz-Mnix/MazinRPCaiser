package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.InputAction;
import br.com.mnix.mazinrpcaiser.common.OpenSessionData;
import br.com.mnix.mazinrpcaiser.common.SessionMetadata;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.*;

public class OpenSessionDataHandlerTest {
	@Test
	public void testProcessAction() throws Exception {
		final IDataGrid dataGrid = DataGridFactory.getGrid();
		final OpenSessionDataHandler handler = new OpenSessionDataHandler();
		final String contextId = "context";
		final String topicId = "topic";
		final SessionMetadata session = new SessionMetadata(contextId, "127.0.0.1");
		final OpenSessionData data1 = new OpenSessionData(false);
		final InputAction action1 = new InputAction(topicId, session, data1);
		final OpenSessionData data2 = new OpenSessionData(true);
		final InputAction action2 = new InputAction(topicId, session, data2);

		dataGrid.raise();
		final Serializable processed1 = handler.processAction(action1, dataGrid);
		dataGrid.retrieveContext(contextId, false).putObject("key1", "bar");
		final Serializable processed2 = handler.processAction(action2, dataGrid);

		assertNull(processed1);
		assertNull(processed2);
		assertEquals(0, dataGrid.retrieveContext(contextId, false).size());

		dataGrid.shutdown();
	}
}