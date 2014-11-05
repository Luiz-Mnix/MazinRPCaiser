package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public abstract class DefaultObjectActionData implements IActionData {
	private static final long serialVersionUID = -6478653069602828014L;

	@Nonnull private final String mObjectId;
	@Nonnull public String getObjectId() {
		return mObjectId;
	}

	protected DefaultObjectActionData(@Nonnull String objectId) {
		mObjectId = objectId;
	}
}
