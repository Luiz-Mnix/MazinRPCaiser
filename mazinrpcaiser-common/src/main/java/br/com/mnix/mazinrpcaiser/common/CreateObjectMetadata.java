package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
@InputMetadata
public class CreateObjectMetadata extends DefaultObjectActionMetadata {
	private static final long serialVersionUID = -8998716644568493004L;

	protected CreateObjectMetadata(@Nonnull String sessionId, @Nonnull String topicId, @Nonnull String objectId,
								   @Nonnull Class serviceClass) {
		super(sessionId, topicId, objectId);
		mServiceClass = serviceClass;
	}

	@Nonnull private final Class mServiceClass;
	@Nonnull public Class getServiceClass() {
		return mServiceClass;
	}
}
