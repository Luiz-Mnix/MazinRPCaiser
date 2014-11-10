package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;
import br.com.mnix.mazinrpcaiser.common.request.Request;
import br.com.mnix.mazinrpcaiser.common.request.RequestUtils;
import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.common.ResponseEnvelope;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import br.com.mnix.mazinrpcaiser.server.service.RequestHasNoServiceException;
import br.com.mnix.mazinrpcaiser.server.service.IService;
import br.com.mnix.mazinrpcaiser.server.service.ServiceFactory;
import org.reflections.Reflections;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public class TaskReceiver implements Runnable {
	@Nonnull private static final Map<Class<?>, TaskReceiver> sRunningReceivers = new WeakHashMap<>();

	@Nonnull private final Class<? extends Serializable> mRequestClass;
	@Nonnull private final IDataGrid mDataGrid;
	@Nonnull private final IService mService;

	private TaskReceiver(@Nonnull Class<? extends Serializable> requestClass, @Nonnull IDataGrid dataGrid)
			throws RequestHasNoServiceException {
		mRequestClass = requestClass;
		mDataGrid = dataGrid;
		mService = ServiceFactory.getServiceForRequest(requestClass);
	}

	@Override
	public void run() {
		BlockingQueue<RequestEnvelope> commands;

		try {
			commands = mDataGrid.getCommandQueue(RequestUtils.getRequestGroup(mRequestClass));
		} catch(Exception ignored) {
			sRunningReceivers.remove(mRequestClass);
			return;
		}

		while (mDataGrid.isOn()) {
			RequestEnvelope requestEnvelope;

			try {
				requestEnvelope = commands.take();
			} catch (InterruptedException ignored) {
				continue;
			}

			Serializable response = null;
			ServerExecutionException exception = null;

			try {
				response = mService.processRequest(requestEnvelope, mDataGrid);
			} catch (Exception e) {
				exception = new ServerExecutionException(e);
			}

			ResponseEnvelope output = new ResponseEnvelope(
					requestEnvelope.getTopicId(),
					requestEnvelope.getSessionData(),
					response,
					exception
			);
			mDataGrid.postNotification(requestEnvelope.getTopicId(), output);
		}

		sRunningReceivers.remove(mRequestClass);
	}

	public static void setUpReceivers(@Nonnull IDataGrid dataGrid) {
		Reflections reflections = new Reflections(MazinRPCaiserConstants.DEFAULT_USER_PACKAGE);

		for(Class<?> requestClass : reflections.getTypesAnnotatedWith(Request.class)) {
			if(Serializable.class.isAssignableFrom(requestClass)) {
				try {
					//noinspection unchecked
					setUpReceiver((Class<? extends Serializable>) requestClass, dataGrid);
				} catch (RequestHasNoServiceException ignored) {
					// TODO log
				}
			}
		}
	}

	public static void setUpReceiver(@Nonnull Class<? extends Serializable> requestClass, @Nonnull IDataGrid dataGrid)
			throws RequestHasNoServiceException {
		if(!sRunningReceivers.containsKey(requestClass)) {
			TaskReceiver taskReceiver = new TaskReceiver(requestClass, dataGrid);
			Thread thread = new Thread(taskReceiver);
			thread.setName(getReceiverName(requestClass));
			thread.start();
			sRunningReceivers.put(requestClass, taskReceiver);
		}
	}

	public static String getReceiverName(@Nonnull Class<? extends Serializable> requestClass) {
		return RequestUtils.getRequestGroup(requestClass);
	}
}
