package br.com.mnix.mazinrpcaiser.client;

import br.com.mnix.mazinrpcaiser.common.*;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeoutException;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public class ServiceClient implements MessageListener {
	private static final int DEFAULT_TIMEOUT = 10 * 1000; // 10 seconds

	@Nonnull private final Map<String, Semaphore> mSemaphores = new HashMap<>();
	@Nonnull private final Map<String, Timer> mTimers = new HashMap<>();
	@Nonnull private final Map<String, OutputAction> mOutputActions = new HashMap<>();
	@Nonnull private final Object mLock = new Object();

	@Nonnull private final IDataGridClient mClient;

	public ServiceClient(@Nonnull IDataGridClient client) {
		mClient = client;
	}

	@Nullable
	public Serializable requestAction(@Nonnull IActionData actionData, @Nonnull SessionMetadata session, int timeout) throws InterruptedException, ServerExecutionException {
		String topicId = UUID.randomUUID().toString();
		Semaphore semaphore = new Semaphore(0);
		mSemaphores.put(topicId, semaphore);

		InputAction inputAction = new InputAction(topicId, session, actionData);
		mClient.addListener(this, topicId);
		mClient.sendData(ActionDataUtils.getActionType(actionData), inputAction);

		new Timer().schedule(new TimeoutTimer(topicId, session), timeout);
		semaphore.acquire();

		OutputAction returnedAction = mOutputActions.get(topicId);
		mOutputActions.remove(topicId);

		if(returnedAction.getException() != null) {
			throw returnedAction.getException();
		}

		return returnedAction.getData();
	}

	@Nullable
	public Serializable requestAction(@Nonnull IActionData actionData, @Nonnull SessionMetadata session)
			throws ServerExecutionException, InterruptedException {
		return requestAction(actionData, session, DEFAULT_TIMEOUT);
	}

	@Nullable
	public <T extends Serializable> T requestAction(@Nonnull IReturn<T> actionData, @Nonnull SessionMetadata session, int timeout)
			throws ServerExecutionException, InterruptedException {
		return (T) requestAction((IActionData) actionData, session, timeout);
	}

	@Nullable
	public <T extends Serializable> T requestAction(@Nonnull IReturn<T> actionData, @Nonnull SessionMetadata session)
			throws ServerExecutionException, InterruptedException {
		return requestAction(actionData, session, DEFAULT_TIMEOUT);
	}

	@Override
	public void onMessage(Message message) {
		synchronized (mLock) {
			OutputAction returnedAction = (OutputAction) message.getMessageObject();

			if(mSemaphores.containsKey(returnedAction.getTopicId())) {
				mOutputActions.put(returnedAction.getTopicId(), returnedAction);

				Semaphore semaphore = mSemaphores.get(returnedAction.getTopicId());
				mSemaphores.remove(returnedAction.getTopicId());
				semaphore.release();
			}
		}
	}

	private class TimeoutTimer extends TimerTask {
		@Nonnull private final String mTopicId;
		@Nonnull private final SessionMetadata mSessionMetadata;

		private TimeoutTimer(@Nonnull String topicId, @Nonnull SessionMetadata sessionMetadata) {
			mTopicId = topicId;
			mSessionMetadata = sessionMetadata;
		}

		@Override
		public void run() {
			synchronized (mLock) {
				if(mSemaphores.containsKey(mTopicId)) {
					Semaphore semaphore = mSemaphores.get(mTopicId);
					mSemaphores.remove(mTopicId);
					mOutputActions.put(
							mTopicId,
							new OutputAction(
									mTopicId,
									mSessionMetadata,
									null,
									new ServerExecutionException(new TimeoutException())
							)
					);
					semaphore.release();
				}
			}
		}
	}
}
