package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
@InputMetadata
public class MethodMetadata extends DefaultObjectActionMetadata {
	private static final long serialVersionUID = 8621393735064551269L;

	@Nonnull
	private final String mMethodName;
	@Nonnull public String getMethodName() {
		return mMethodName;
	}

	protected MethodMetadata(@Nonnull String sessionId, @Nonnull String topicId, @Nonnull String objectId,
							 @Nonnull String methodName) {
		super(sessionId, topicId, objectId);
		mMethodName = methodName;
	}
}
