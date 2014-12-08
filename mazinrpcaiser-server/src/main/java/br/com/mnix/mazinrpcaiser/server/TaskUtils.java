package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.ResponseEnvelope;
import br.com.mnix.mazinrpcaiser.common.SessionData;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Created by mnix05 on 12/5/14.
 *
 * @author mnix05
 */
public final class TaskUtils {
	private TaskUtils() {}

	public static void returnToRequester(@Nonnull IDataGrid grid, @Nonnull String topicId,
										 @Nonnull SessionData sessionData, @Nullable Serializable response,
										 @Nullable Throwable exception) {
		ResponseEnvelope output = new ResponseEnvelope(
				topicId,
				sessionData,
				response,
				exception != null ? new ServerExecutionException(exception) : null
		);

		if(grid.isOn()) {
			try {
				grid.postNotification(topicId, output);
			} catch (IllegalStateException ignored) {}
		}
	}
}
