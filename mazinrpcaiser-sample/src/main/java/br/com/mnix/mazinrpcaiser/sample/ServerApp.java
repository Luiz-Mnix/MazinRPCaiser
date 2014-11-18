package br.com.mnix.mazinrpcaiser.sample;

import br.com.mnix.mazinrpcaiser.client.ServiceClient;
import br.com.mnix.mazinrpcaiser.client.Session;
import br.com.mnix.mazinrpcaiser.common.NotTransmissibleObject;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.common.request.CreateObjectRequest;
import br.com.mnix.mazinrpcaiser.common.request.MethodRequest;
import br.com.mnix.mazinrpcaiser.sample.interfaces.IDistributedSample;
import br.com.mnix.mazinrpcaiser.server.TaskReceiver;
import br.com.mnix.mazinrpcaiser.server.data.DataGridFactory;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public final class ServerApp {
	@Nonnull public static final String SESSION_ID = "session";
	@Nonnull public static final String SERVER_ADDRESS = "127.0.0.1";
	@Nonnull public static final String MAIN_OBJ_ID = "OBJ";

	@SuppressWarnings("RedundantThrows")
	public static void main(String[] args) throws Exception {
		IDataGrid grid = DataGridFactory.getGrid();
		grid.raise();
		TaskReceiver.setUpReceivers(grid);
		openSession1(grid);
		grid.shutdown();
	}

	private static void openSession1(IDataGrid grid) throws Exception {
		Session session = new Session(SESSION_ID, SERVER_ADDRESS);
		session.open(true);
		ServiceClient client = new ServiceClient(session.getDataGridClient());

		CreateObjectRequest createObjectRequest = new CreateObjectRequest(MAIN_OBJ_ID, IDistributedSample.class);
		client.makeRequest(createObjectRequest, session.getSessionData());

		int n1 = 1, n2 = 2;
		MethodRequest methodRequest = new MethodRequest(MAIN_OBJ_ID, "add", n1, n2);
		int resultAdd = (int) client.makeRequest(methodRequest, session.getSessionData());
		System.out.println(String.format("%d + %d = %d", n1, n2, resultAdd));

		String s1 = "foo", s2 = "bar";
		MethodRequest methodRequest1 = new MethodRequest(MAIN_OBJ_ID, "concat", s1, s2);
		String resultConcat = (String) client.makeRequest(methodRequest1, session.getSessionData());
		System.out.println(String.format("\"%s\" + \"%s\" = \"%s\"", s1, s2, resultConcat));

		MethodRequest methodRequest2 = new MethodRequest(MAIN_OBJ_ID, "concat", s1, n1);
		String resultConcat1 = (String) client.makeRequest(methodRequest2, session.getSessionData());
		System.out.println(String.format("\"%s\" + %d = \"%s\"", s1, n1, resultConcat1));

		MethodRequest methodRequest3 = new MethodRequest(MAIN_OBJ_ID, "repeat", n1);
		int[] resultRepeat = (int[]) client.makeRequest(methodRequest3, session.getSessionData());
		System.out.println(String.format("Repeating %d = %s", n1, Arrays.toString(resultRepeat)));

		MethodRequest methodRequest4 = new MethodRequest(MAIN_OBJ_ID, "repeat", s1);
		List<String> resultRepeat1 = (List<String>) client.makeRequest(methodRequest4, session.getSessionData());
		assert resultRepeat1 != null;
		System.out.println(String.format("Repeating %s = %s", s1, resultRepeat1.toString()));

		MethodRequest methodRequest5 = new MethodRequest(MAIN_OBJ_ID, "repeat", s1, n1);
		Map<String, Integer> resultRepeat2 = (Map<String, Integer> ) client.makeRequest(methodRequest5, session.getSessionData());
		System.out.println(String.format("Repeating %s, %d = %s", s1, n1, resultRepeat2));

		try {
			MethodRequest methodRequest7 = new MethodRequest(MAIN_OBJ_ID, "throwException", true);
			client.makeRequest(methodRequest7, session.getSessionData());
		} catch (ServerExecutionException ex) {
			ex.printStackTrace();
		}

		MethodRequest methodRequest6 = new MethodRequest(MAIN_OBJ_ID, "createSubSample", s2);
		NotTransmissibleObject resultSubSample = (NotTransmissibleObject) client.makeRequest(methodRequest6, session.getSessionData());
		assert resultSubSample != null;
		concurrentAccess(resultSubSample.getObjectId(), grid);

		session.invalidate();
	}

	private static void concurrentAccess(String id, IDataGrid grid) throws Exception {
		Session session = new Session(SESSION_ID, SERVER_ADDRESS);
		session.open(false);
		ServiceClient client = new ServiceClient(session.getDataGridClient());
		MethodRequest methodRequest = new MethodRequest(id, "getFoo");
		Serializable resultGetFoo = client.makeRequest(methodRequest, session.getSessionData());
		System.out.println("Getting Foo: " + resultGetFoo);
		session.invalidate();
	}
}
