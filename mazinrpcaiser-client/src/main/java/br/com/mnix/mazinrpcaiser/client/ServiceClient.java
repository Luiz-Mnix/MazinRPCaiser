package br.com.mnix.mazinrpcaiser.client;

import br.com.mnix.mazinrpcaiser.common.*;
import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;
import br.com.mnix.mazinrpcaiser.common.request.IReturn;
import br.com.mnix.mazinrpcaiser.common.request.RequestUtils;
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
public class ServiceClient implements IServiceClient, MessageListener {
	private static final int DEFAULT_TIMEOUT = 10 * 1000; // 10 seconds

	@Nonnull private final Map<String, Semaphore> mSemaphores = new HashMap<>();
	@Nonnull private final Map<String, ResponseEnvelope> mOutputActions = new HashMap<>();
	@Nonnull private final Object mLock = new Object();

	@Nonnull private final IDataGridClient mClient;

	public ServiceClient(@Nonnull IDataGridClient client) {
		mClient = client;
	}

	@Override
	@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
	@Nullable
	public Serializable makeRequest(@Nonnull Serializable request, @Nonnull SessionData session, int timeout)
			throws InterruptedException, ServerExecutionException {
		String topicId = UUID.randomUUID().toString();
		Semaphore semaphore = new Semaphore(0);
		mSemaphores.put(topicId, semaphore);

		RequestEnvelope requestEnvelope = new RequestEnvelope(topicId, session, request);
		String listenerId = mClient.addListener(topicId, this);
		mClient.sendData(RequestUtils.getRequestGroup(request), requestEnvelope);

		new Timer().schedule(new TimeoutTimer(topicId, session), timeout);
		semaphore.acquire();

		mClient.removeListener(topicId, listenerId);
		ResponseEnvelope returnedAction = mOutputActions.get(topicId);
		mOutputActions.remove(topicId);

		if(returnedAction.getException() != null) {
			throw returnedAction.getException();
		}

		return returnedAction.getResponse();
	}

	@Override
	@Nullable
	public Serializable makeRequest(@Nonnull Serializable actionData, @Nonnull SessionData session)
			throws ServerExecutionException, InterruptedException {
		return makeRequest(actionData, session, DEFAULT_TIMEOUT);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public <T extends Serializable> T makeRequest(@Nonnull IReturn<T> actionData, @Nonnull SessionData session, int timeout)
			throws ServerExecutionException, InterruptedException {
		return (T) makeRequest((Serializable) actionData, session, timeout);
	}

	@Override
	@Nullable
	public <T extends Serializable> T makeRequest(@Nonnull IReturn<T> actionData, @Nonnull SessionData session)
			throws ServerExecutionException, InterruptedException {
		return makeRequest(actionData, session, DEFAULT_TIMEOUT);
	}

	@Override
	public void onMessage(Message message) {
		synchronized (mLock) {
			ResponseEnvelope returnedAction = (ResponseEnvelope) message.getMessageObject();

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
		@Nonnull private final SessionData mSessionData;

		private TimeoutTimer(@Nonnull String topicId, @Nonnull SessionData sessionData) {
			mTopicId = topicId;
			mSessionData = sessionData;
		}

		@Override
		public void run() {
			synchronized (mLock) {
				if(mSemaphores.containsKey(mTopicId)) {
					Semaphore semaphore = mSemaphores.get(mTopicId);
					mSemaphores.remove(mTopicId);
					mOutputActions.put(
							mTopicId,
							new ResponseEnvelope(
									mTopicId,
									mSessionData,
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
