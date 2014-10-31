package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
@InputMetadata
public class OpenSessionMetadata extends DefaultActionMetadata {
	private static final long serialVersionUID = -5448964193585558040L;

	private final boolean mOverwritesExisting;
	public boolean getOverwritesExisting() {
		return mOverwritesExisting;
	}

	protected OpenSessionMetadata(@Nonnull String sessionId, @Nonnull String topicId, boolean overwritesExisting) {
		super(sessionId, topicId);
		mOverwritesExisting = overwritesExisting;
	}

	protected OpenSessionMetadata(@Nonnull String sessionId, @Nonnull String topicId) {
		super(sessionId, topicId);
		mOverwritesExisting = false;
	}
}
