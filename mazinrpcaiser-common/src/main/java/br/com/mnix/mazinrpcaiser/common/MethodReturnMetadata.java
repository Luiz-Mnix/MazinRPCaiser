package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
@OutputMetadata
public class MethodReturnMetadata extends DefaultReturnMetadata {
	private static final long serialVersionUID = -7339367131363804321L;

	protected MethodReturnMetadata(@Nonnull String sessionId, @Nonnull String topicId,
								   @Nonnull String objectId, Exception exception, Serializable returnedData) {
		super(sessionId, topicId, exception);
		mObjectId = objectId;
		mReturnedData = returnedData;
	}

	@Nonnull private final String mObjectId;
	@Nonnull public String getObjectId() {
		return mObjectId;
	}

	@Nullable
	private final Serializable mReturnedData;
	@Nullable public Serializable getReturnedData() {
		return mReturnedData;
	}
}
