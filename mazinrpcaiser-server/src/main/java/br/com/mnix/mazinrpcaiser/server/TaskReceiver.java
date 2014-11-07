package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.request.RequestUtils;
import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.common.ResponseEnvelope;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import br.com.mnix.mazinrpcaiser.server.service.RequestHasNoServiceException;
import br.com.mnix.mazinrpcaiser.server.service.IService;
import br.com.mnix.mazinrpcaiser.server.service.ServiceFactory;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public class TaskReceiver<TMetadata extends Serializable> implements Runnable {
	private final Class<TMetadata> mRequestClass;
	private final IDataGrid mDataGrid;
	private final IService mService;

	public TaskReceiver(Class<TMetadata> requestClass, IDataGrid dataGrid) throws RequestHasNoServiceException {
		mRequestClass = requestClass;
		mDataGrid = dataGrid;
		mService = ServiceFactory.getServiceForRequest(requestClass);
	}

	@Override
	public void run() {
		BlockingQueue<RequestEnvelope> commands
				= mDataGrid.getCommandQueue(RequestUtils.getRequestGroup(mRequestClass));

		while (mDataGrid.isOn()) {
			RequestEnvelope requestEnvelope = null;
			Serializable response = null;
			ServerExecutionException exception = null;
			try {
				requestEnvelope = commands.take();
				response = mService.processRequest(requestEnvelope, mDataGrid);
			} catch (InterruptedException ignored) {
				continue;
			} catch (Exception ex) {
				exception = new ServerExecutionException(ex);
			}
			assert requestEnvelope != null;
			ResponseEnvelope output = new ResponseEnvelope(
					requestEnvelope.getTopicId(),
					requestEnvelope.getSessionData(),
					response,
					exception
			);
			mDataGrid.postNotification(requestEnvelope.getTopicId(), output);
		}
	}
}
