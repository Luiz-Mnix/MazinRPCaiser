package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.ActionDataUtils;
import br.com.mnix.mazinrpcaiser.common.IActionData;
import br.com.mnix.mazinrpcaiser.common.InputAction;
import br.com.mnix.mazinrpcaiser.common.OutputAction;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public class TaskReceiver<TMetadata extends IActionData> implements Runnable {
	private final Class<TMetadata> mMetadataType;
	private final IDataGrid mDataGrid;
	private final IActionHandler mActionHandler;

	public TaskReceiver(Class<TMetadata> metadataType, IDataGrid dataGrid) {
		mMetadataType = metadataType;
		mDataGrid = dataGrid;
		mActionHandler = ActionHandlerFactory.handlerForActionDataType(metadataType);
	}

	@Override
	public void run() {
		BlockingQueue<InputAction> commands = mDataGrid.getCommandQueue(ActionDataUtils.getActionType(mMetadataType));
		while (mDataGrid.isOn()) {
			InputAction action = null;
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
			OutputAction output = new OutputAction(
					action.getTopicId(),
					action.getSessionMetadata(),
					processedData,
					exception
			);
			mDataGrid.postNotification(action.getTopicId(), output);
		}
	}
}
