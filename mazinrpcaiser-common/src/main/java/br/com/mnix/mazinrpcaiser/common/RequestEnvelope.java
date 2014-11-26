package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public final class RequestEnvelope implements Serializable {
	private static final long serialVersionUID = -3062733748255414363L;

	@Nonnull private final String mTopicId;
	@Nonnull private final SessionData mSessionData;
	@Nonnull private final Serializable mRequest;

	public RequestEnvelope(@Nonnull String topicId, @Nonnull SessionData sessionData,
						   @Nonnull Serializable request) {
		mTopicId = topicId;
		mRequest = request;
		mSessionData = sessionData;
	}

	@Nonnull public SessionData getSessionData() {
		return mSessionData;
	}
	@Nonnull public String getTopicId() {
		return mTopicId;
	}
	@Nonnull public Serializable getRequest() {
		return mRequest;
	}
}
