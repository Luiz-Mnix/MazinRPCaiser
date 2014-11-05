package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public final class InputAction implements Serializable {
	private static final long serialVersionUID = -3062733748255414363L;

	@Nonnull private final String mTopicId;
	@Nonnull private final SessionMetadata mSessionMetadata;
	@Nonnull private final IActionData mActionData;

	public InputAction(@Nonnull String topicId, @Nonnull SessionMetadata sessionMetadata,
					   @Nonnull IActionData iActionData) {
		mTopicId = topicId;
		mActionData = iActionData;
		mSessionMetadata = sessionMetadata;
	}

	@Nonnull public SessionMetadata getSessionMetadata() {
		return mSessionMetadata;
	}
	@Nonnull public String getTopicId() {
		return mTopicId;
	}
	@Nonnull public IActionData getActionData() {
		return mActionData;
	}
}
