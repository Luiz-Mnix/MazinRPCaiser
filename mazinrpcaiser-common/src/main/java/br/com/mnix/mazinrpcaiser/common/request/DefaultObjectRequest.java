package br.com.mnix.mazinrpcaiser.common.request;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public abstract class DefaultObjectRequest implements Serializable {
	private static final long serialVersionUID = -6478653069602828014L;

	@Nonnull private final String mObjectId;
	@Nonnull public String getObjectId() {
		return mObjectId;
	}

	protected DefaultObjectRequest(@Nonnull String objectId) {
		mObjectId = objectId;
	}
}
