package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
@OutputActionData
public class MethodReturnData implements Serializable {
	private static final long serialVersionUID = -7339367131363804321L;

	protected MethodReturnData(@Nonnull String objectId, Serializable returnedData) {
		mObjectId = objectId;
		mReturnedData = returnedData;
	}

	@Nonnull private final String mObjectId;
	@Nonnull public String getObjectId() {
		return mObjectId;
	}

	@Nullable private final Serializable mReturnedData;
	@Nullable public Serializable getReturnedData() {
		return mReturnedData;
	}
}
