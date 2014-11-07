package br.com.mnix.mazinrpcaiser.server.data;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public final class DataGridFactory {
	private DataGridFactory() {}

	// TODO implement
	public static IDataGrid getGrid() {
		return new DataGrid();
	}
}
