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
public class OutputAction implements Serializable {
	private static final long serialVersionUID = -3062733748255414363L;

	@Nonnull private final String mTopicId;
	@Nonnull private final SessionMetadata mSessionMetadata;
	@Nullable private final Serializable mData;
	@Nullable private final ServerExecutionException mException;

	public OutputAction(@Nonnull String topicId, @Nonnull SessionMetadata sessionMetadata,
						@Nullable Serializable data, @Nullable ServerExecutionException exception) {
		mTopicId = topicId;
		mSessionMetadata = sessionMetadata;
		mData = data;
		mException = exception;
	}

	@Nonnull public SessionMetadata getSessionMetadata() {
		return mSessionMetadata;
	}
	@Nonnull public String getTopicId() {
		return mTopicId;
	}
	@Nullable public Serializable getData() {
		return mData;
	}
	@Nullable public ServerExecutionException getException() {
		return mException;
	}
}
