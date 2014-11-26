package br.com.mnix.mazinrpcaiser.common;

import br.com.mnix.mazinrpcaiser.common.exception.ServerExecutionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public class ResponseEnvelope implements Serializable {
	private static final long serialVersionUID = -3062733748255414363L;

	@Nonnull private final String mTopicId;
	@Nonnull private final SessionData mSessionData;
	@Nullable private final Serializable mResponse;
	@Nullable private final ServerExecutionException mException;

	public ResponseEnvelope(@Nonnull String topicId, @Nonnull SessionData sessionData,
							@Nullable Serializable response, @Nullable ServerExecutionException exception) {
		mTopicId = topicId;
		mSessionData = sessionData;
		mResponse = response;
		mException = exception;
	}

	@Nonnull public SessionData getSessionData() {
		return mSessionData;
	}
	@Nonnull public String getTopicId() {
		return mTopicId;
	}
	@Nullable public Serializable getResponse() {
		return mResponse;
	}
	@Nullable public ServerExecutionException getException() {
		return mException;
	}
}
