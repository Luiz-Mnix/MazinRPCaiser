package br.com.mnix.mazinrpcaiser.sample.concurrent;

import br.com.mnix.mazinrpcaiser.client.Session;
import br.com.mnix.mazinrpcaiser.client.proxy.IProxyFactory;
import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;
import br.com.mnix.mazinrpcaiser.server.TaskReceiver;
import br.com.mnix.mazinrpcaiser.server.data.DataGridFactory;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;

/**
 * Created by mnix05 on 12/4/14.
 *
 * @author mnix05
 */
public final class ConcurrentApp {
	public static final String SESSION_ID = "session";
	public static final String OBJ_ID = "obj";
	public static final String OBJ_ID2 = "obj2";
	public static final int TIMES_TO_PRINT = 5;

	public static void main(String[] args) throws Exception {
		final IDataGrid grid1 = DataGridFactory.makeDefaultDataGrid();
		final IDataGrid grid2 = DataGridFactory.makeDataGrid(MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS + ":5702");

		grid1.raise();
		grid2.raise();

		TaskReceiver.setUpTaskReceiver(grid1);
		TaskReceiver.setUpTaskReceiver(grid2);

		Session session1 = new Session(SESSION_ID, MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS);
		final Session session2 = new Session(SESSION_ID, MazinRPCaiserConstants.DEFAULT_SERVER_ADDRESS + ":5702");

		session1.open(false);
		session2.open(false);

		IProxyFactory factory1 = session1.getProxyFactory();
		IProxyFactory factory2 = session2.getProxyFactory();

		final IDistributedSample sample1 = factory1.makeRemoteProxy(OBJ_ID, false, IDistributedSample.class, OBJ_ID);
		final IDistributedSample sample2 = factory2.makeRemoteProxy(OBJ_ID2, false, IDistributedSample.class, OBJ_ID2);

		Thread thread1 = new Thread(new Runnable() {
			@Override
			public void run() {
				sample1.print(TIMES_TO_PRINT);
				try {
					session2.invalidate(false);
				} catch (Exception ignored) {}
				grid1.shutdown();
			}
		});
		Thread thread2 = new Thread(new Runnable() {
			@Override
			public void run() {
				sample2.print(TIMES_TO_PRINT);
				try {
					session2.invalidate(false);
				} catch (Exception ignored) {}
				grid2.shutdown();
			}
		});

		thread1.start();
		thread2.start();
	}
}
