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
	private final Class<TMetadata> mMetadataType;
	private final IDataGrid mDataGrid;
	private final IService mActionHandler;

	public TaskReceiver(Class<TMetadata> metadataType, IDataGrid dataGrid) throws RequestHasNoServiceException {
		mMetadataType = metadataType;
		mDataGrid = dataGrid;
		mActionHandler = ServiceFactory.getServiceForRequest(metadataType);
	}

	@Override
	public void run() {
		BlockingQueue<RequestEnvelope> commands = mDataGrid.getCommandQueue(RequestUtils.getActionType(mMetadataType));
		while (mDataGrid.isOn()) {
			RequestEnvelope action = null;
			Serializable processedData = null;
			ServerExecutionException exception = null;
			try {
				action = commands.take();
				processedData = mActionHandler.processAction(action, mDataGrid);
				// TODO translate
			} catch (InterruptedException ignored) {
				continue;
			} catch (Exception ex) {
				exception = new ServerExecutionException(ex);
			}
			assert action != null;
			ResponseEnvelope output = new ResponseEnvelope(
					action.getTopicId(),
					action.getSessionData(),
					processedData,
					exception
			);
			mDataGrid.postNotification(action.getTopicId(), output);
		}
	}
}
