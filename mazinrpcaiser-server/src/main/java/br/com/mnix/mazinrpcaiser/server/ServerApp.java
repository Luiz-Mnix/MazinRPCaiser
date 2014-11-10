package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.server.data.DataGridFactory;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public final class ServerApp {
	@SuppressWarnings("RedundantThrows")
	public static void main(String[] args) throws Exception {
		IDataGrid grid = DataGridFactory.getGrid();
		grid.raise();
		TaskReceiver.setUpReceivers(grid);
	}
}
