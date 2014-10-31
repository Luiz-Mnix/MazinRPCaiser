package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.apache.commons.lang3.Validate.*;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
@OutputMetadata
public class DefaultReturnMetadata extends DefaultActionMetadata {
	private static final long serialVersionUID = -7278996429233083088L;

	protected DefaultReturnMetadata(@Nonnull String sessionId, @Nonnull String topicId, Exception exception) {
		super(sessionId, topicId);
		mException = exception;
	}

	@Nullable
	private final Exception mException;
	@Nullable public Exception getException() {
		return mException;
	}
}
