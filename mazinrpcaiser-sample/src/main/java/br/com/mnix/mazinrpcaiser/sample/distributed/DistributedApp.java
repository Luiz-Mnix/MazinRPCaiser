package br.com.mnix.mazinrpcaiser.sample.distributed;

import br.com.mnix.mazinrpcaiser.client.Session;
import br.com.mnix.mazinrpcaiser.client.proxy.IProxyFactory;
import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;
import br.com.mnix.mazinrpcaiser.server.TaskReceiver;
import br.com.mnix.mazinrpcaiser.server.data.DataGridFactory;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import org.apache.commons.lang3.Validate;

/**
 * Created by mnix05 on 11/27/14.
 *
 * @author mnix05
 */
public final class DistributedApp {
	private static final String SESSION_ID = "foo";
	private static final String OBJ_ID = "obj";
	private static final String OBJ_ID2 = "obj2";

	public static void main (String[] args) throws Exception {
		IDataGrid grid1 = DataGridFactory.makeDefaultDataGrid();
		grid1.raise();
		TaskReceiver.setUpTaskReceiver(grid1);

		IDataGrid grid2 = DataGridFactory.makeDataGrid(MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS + ":5702");
		grid2.raise();
		TaskReceiver.setUpTaskReceiver(grid2);

		IDataGrid grid3 = DataGridFactory.makeDataGrid(MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS + ":5703");
		grid3.raise();
		TaskReceiver.setUpTaskReceiver(grid3);

		Session session1 = new Session(SESSION_ID, MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS);
		Session session2 = new Session(SESSION_ID, MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS + ":5702");
		Session session3 = new Session(SESSION_ID, MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS + ":5703");

		session1.open(false);
		session2.open(false);
		session3.open(false);

		IProxyFactory factory1 = session1.getProxyFactory();
		IProxyFactory factory2 = session2.getProxyFactory();
		IProxyFactory factory3 = session3.getProxyFactory();

		int n = 2;

		IDistributedSample sample1 = factory1.makeRemoteProxy(OBJ_ID, true, IDistributedSample.class);
		sample1.setCode(n);

		IDistributedSample sample1_2 = factory2.makeProxy(OBJ_ID, IDistributedSample.class);
		System.out.println(String.format("Expects n = %d. Actual n = %d", sample1.getCode(), sample1_2.getCode()));
		Validate.isTrue(n == sample1_2.getCode());

		IDistributedSample sample2_2 = factory2.makeRemoteProxy(OBJ_ID2, false, IDistributedSample.class);
		sample2_2.setCode(n * n);

		session1.invalidate(false);
		session2.invalidate(false);

		IDistributedSample sample1_3 = factory3.makeProxy(OBJ_ID, IDistributedSample.class);
		IDistributedSample sample2_3 = factory3.makeProxy(OBJ_ID2, IDistributedSample.class);

		System.out.println(String.format("Sample 1: Expects n = %d. Actual n = %d", n, sample1_3.getCode()));
		System.out.println(String.format("Sample 2: Expects n = %d. Actual n = %d", n * n, sample2_3.getCode()));

		session3.invalidate(false);

		grid1.shutdown();
		grid2.shutdown();
		grid3.shutdown();
	}
}
