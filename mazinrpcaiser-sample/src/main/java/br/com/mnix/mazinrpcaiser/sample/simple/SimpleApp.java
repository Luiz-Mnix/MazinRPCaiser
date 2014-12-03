package br.com.mnix.mazinrpcaiser.sample.simple;

import br.com.mnix.mazinrpcaiser.client.Session;
import br.com.mnix.mazinrpcaiser.client.proxy.IProxy;
import br.com.mnix.mazinrpcaiser.client.proxy.IProxyFactory;
import br.com.mnix.mazinrpcaiser.client.web.IServiceClient;
import br.com.mnix.mazinrpcaiser.client.web.ServiceClient;
import br.com.mnix.mazinrpcaiser.common.ProxiedObject;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.common.request.CreateObjectRequest;
import br.com.mnix.mazinrpcaiser.common.request.MethodRequest;
import br.com.mnix.mazinrpcaiser.sample.simple.interfaces.IDistributedSample;
import br.com.mnix.mazinrpcaiser.sample.simple.interfaces.IDistributedSubSample;
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
public final class SimpleApp {
	@Nonnull public static final String SESSION_ID = "session";
	@Nonnull public static final String SESSION_ID2 = "session2";
	@Nonnull public static final String SERVER_ADDRESS = "127.0.0.1";
	@Nonnull public static final String MAIN_OBJ_ID = "OBJ";

	public static void main(String[] args) throws Exception {
		IDataGrid grid = DataGridFactory.makeDefaultDataGrid();
		grid.raise();
		TaskReceiver.setUpReceivers(grid);
		openSession1();
		openSession2();
		grid.shutdown();
	}

	private static void openSession1() throws Exception {
		Session session = new Session(SESSION_ID, SERVER_ADDRESS);
		session.open(true);
		IServiceClient client = new ServiceClient(session.getSessionData(), session.getDataGridClient());

		CreateObjectRequest createObjectRequest = new CreateObjectRequest(MAIN_OBJ_ID, true, IDistributedSample.class);
		client.makeRequest(createObjectRequest);

		int n1 = 1, n2 = 2;
		MethodRequest methodRequest = new MethodRequest(MAIN_OBJ_ID, "add", n1, n2);
		int resultAdd = (int) client.makeRequest(methodRequest);
		System.out.println(String.format("%d + %d = %d", n1, n2, resultAdd));

		String s1 = "foo", s2 = "bar";
		MethodRequest methodRequest1 = new MethodRequest(MAIN_OBJ_ID, "concat", s1, s2);
		String resultConcat = (String) client.makeRequest(methodRequest1);
		System.out.println(String.format("\"%s\" + \"%s\" = \"%s\"", s1, s2, resultConcat));

		MethodRequest methodRequest2 = new MethodRequest(MAIN_OBJ_ID, "concat", s1, n1);
		String resultConcat1 = (String) client.makeRequest(methodRequest2);
		System.out.println(String.format("\"%s\" + %d = \"%s\"", s1, n1, resultConcat1));

		MethodRequest methodRequest3 = new MethodRequest(MAIN_OBJ_ID, "repeat", n1);
		int[] resultRepeat = (int[]) client.makeRequest(methodRequest3);
		System.out.println(String.format("Repeating %d = %s", n1, Arrays.toString(resultRepeat)));

		MethodRequest methodRequest4 = new MethodRequest(MAIN_OBJ_ID, "repeat", s1);
		@SuppressWarnings("unchecked") List<String> resultRepeat1 = (List<String>) client.makeRequest(methodRequest4);
		assert resultRepeat1 != null;
		System.out.println(String.format("Repeating %s = %s", s1, resultRepeat1.toString()));

		MethodRequest methodRequest5 = new MethodRequest(MAIN_OBJ_ID, "repeat", s1, n1);
		@SuppressWarnings("unchecked") Map<String, Integer> resultRepeat2 = (Map<String, Integer>) client.makeRequest(methodRequest5);
		System.out.println(String.format("Repeating %s, %d = %s", s1, n1, resultRepeat2));

		try {
			MethodRequest methodRequest7 = new MethodRequest(MAIN_OBJ_ID, "throwException", true);
			client.makeRequest(methodRequest7);
		} catch (ServerExecutionException ex) {
			System.out.println("Received an expected exception: " + ex.getCause().getLocalizedMessage());
		}

		MethodRequest methodRequest6 = new MethodRequest(MAIN_OBJ_ID, "createSubSample", s2);
		ProxiedObject resultSubSample = (ProxiedObject) client.makeRequest(methodRequest6);
		assert resultSubSample != null;
		concurrentAccess(resultSubSample.getObjectId());

		session.invalidate(true);
	}

	private static void concurrentAccess(String id) throws Exception {
		Session session = new Session(SESSION_ID, SERVER_ADDRESS);
		session.open(false);
		ServiceClient client = new ServiceClient(session.getSessionData(), session.getDataGridClient());
		MethodRequest methodRequest = new MethodRequest(id, "getFoo");
		Serializable resultGetFoo = client.makeRequest(methodRequest);
		System.out.println("Getting Foo: " + resultGetFoo);
		session.invalidate(false);
	}

	private static void openSession2() throws Exception {
		Session session = new Session(SESSION_ID2, SERVER_ADDRESS);
		session.open(true);
		IProxyFactory factory = session.getProxyFactory();
		IDistributedSample sample = factory.makeRemoteProxy(MAIN_OBJ_ID, true, IDistributedSample.class);

		int n1 = 4, n2 = 6;
		int resultAdd = sample.add(n1, n2);
		System.out.println(String.format("%d + %d = %d", n1, n2, resultAdd));

		String s1 = "zap", s2 = "zup";
		String resultConcat = sample.concat(s1, s2);
		System.out.println(String.format("\"%s\" + \"%s\" = \"%s\"", s1, s2, resultConcat));

		String resultConcat1 = sample.concat(s1, n1);
		System.out.println(String.format("\"%s\" + %d = \"%s\"", s1, n1, resultConcat1));

		int[] resultRepeat = sample.repeat(n1);
		System.out.println(String.format("Repeating %d = %s", n1, Arrays.toString(resultRepeat)));

		List<String> resultRepeat1 = sample.repeat(s1);
		System.out.println(String.format("Repeating %s = %s", s1, resultRepeat1.toString()));

		Map<String, Integer> resultRepeat2 = sample.repeat(s1, n1);
		System.out.println(String.format("Repeating %s, %d = %s", s1, n1, resultRepeat2));

		try {
			sample.throwException(true);
		} catch (Exception ex) {
			System.out.println("Received an expected exception: " + ex.getLocalizedMessage());
		}

		IDistributedSubSample subSample = sample.createSubSample(s2);
		IProxy proxy = factory.getProxyOfObject(subSample);
		concurrentAccess2(proxy.getId());

		session.invalidate(true);
	}

	private static void concurrentAccess2(String id) throws Exception {
		Session session = new Session(SESSION_ID2, SERVER_ADDRESS);
		session.open(false);
		IProxyFactory factory = session.getProxyFactory();
		IDistributedSubSample subSample = factory.makeRemoteProxy(id, false, IDistributedSubSample.class);
		Serializable resultGetFoo = subSample.getFoo();
		System.out.println("Getting Foo: " + resultGetFoo);
		session.invalidate(false);
	}
}
