package br.com.mnix.mazinrpcaiser.server.data;

import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;

import javax.annotation.Nonnull;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public final class DataGridFactory {
	@Nonnull public static final String DEFAULT_NETWORK_INTERFACE = MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS;

	private DataGridFactory() {}

	public static IDataGrid makeDataGrid(String networkInterface) {
		return new DataGrid(networkInterface);
	}
	public static IDataGrid makeDefaultDataGrid() {
		return makeDataGrid(DEFAULT_NETWORK_INTERFACE);
	}
}
