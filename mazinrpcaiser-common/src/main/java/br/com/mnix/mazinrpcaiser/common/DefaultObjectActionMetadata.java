package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;

import static org.apache.commons.lang3.Validate.*;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public abstract class DefaultObjectActionMetadata extends DefaultActionMetadata {
	@Nonnull
	private final String mObjectId;
	@Nonnull public String getObjectId() {
		return mObjectId;
	}

	protected DefaultObjectActionMetadata(@Nonnull String sessionId, @Nonnull String topicId,
										  @Nonnull String objectId) {
		super(sessionId, topicId);
		mObjectId = objectId;
	}
}
