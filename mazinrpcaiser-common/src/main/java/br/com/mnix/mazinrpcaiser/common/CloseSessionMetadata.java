package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
@InputMetadata
public class CloseSessionMetadata extends DefaultActionMetadata {
	private static final long serialVersionUID = 6834118314752803735L;

	protected CloseSessionMetadata(@Nonnull String sessionId, @Nonnull String topicId) {
		super(sessionId, topicId);
	}
}
