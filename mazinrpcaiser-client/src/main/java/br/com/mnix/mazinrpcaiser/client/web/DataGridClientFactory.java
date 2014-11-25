package br.com.mnix.mazinrpcaiser.client.web;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public final class DataGridClientFactory {
	public static IDataGridClient createDataGrid(String clusterAddress) {
		return new DataGridClient(clusterAddress);
	}
}
