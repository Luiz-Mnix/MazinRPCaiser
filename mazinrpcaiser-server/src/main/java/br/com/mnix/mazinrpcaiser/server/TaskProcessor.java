package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import br.com.mnix.mazinrpcaiser.server.service.IService;
import br.com.mnix.mazinrpcaiser.server.service.RequestHasNoServiceException;
import br.com.mnix.mazinrpcaiser.server.service.ServiceFactory;

import javax.annotation.Nonnull;
import java.io.Serializable;

import static br.com.mnix.mazinrpcaiser.server.TaskUtils.returnToRequester;

/**
 * Created by mnix05 on 12/4/14.
 *
 * @author mnix05
 */
public class TaskProcessor implements Runnable {
	@Nonnull private final RequestEnvelope mRequestEnvelope;

	@Nonnull private final IDataGrid mDataGrid;

	public TaskProcessor(@Nonnull RequestEnvelope requestEnvelope, @Nonnull IDataGrid dataGrid) {
		mRequestEnvelope = requestEnvelope;
		mDataGrid = dataGrid;
	}

	@Override
	public void run() {
		IService service;

		try {
			 service = ServiceFactory.getServiceForRequest(mRequestEnvelope.getRequest().getClass());
		} catch (RequestHasNoServiceException e) {
			returnToRequester(mDataGrid, mRequestEnvelope.getTopicId(), mRequestEnvelope.getSessionData(), null, e);
			return;
		}

		Serializable response = null;
		Throwable exception = null;

		try {
			response = service.processRequest(mRequestEnvelope, mDataGrid);
		} catch (Throwable e) {
			exception = e;
		}

		returnToRequester(
				mDataGrid, mRequestEnvelope.getTopicId(), mRequestEnvelope.getSessionData(), response, exception
		);
	}
}
