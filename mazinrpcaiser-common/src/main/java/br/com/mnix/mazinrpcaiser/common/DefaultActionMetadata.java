package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;

import java.util.UUID;

import static org.apache.commons.lang3.Validate.*;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public abstract class DefaultActionMetadata implements IActionMetadata {
	@Nonnull
	private final String mSessionId;
	@Nonnull public String getSessionId() {
		return mSessionId;
	}

	@Nonnull
	private final String mTopicId;
	@Nonnull public String getTopicId() {
		return mTopicId;
	}

	protected DefaultActionMetadata(@Nonnull String sessionId, @Nonnull String topicId) {
		mSessionId = sessionId;
		mTopicId = topicId;
	}
}
