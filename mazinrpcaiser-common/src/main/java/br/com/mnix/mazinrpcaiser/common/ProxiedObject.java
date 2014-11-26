package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/10/14.
 *
 * @author mnix05
 */
public class ProxiedObject implements Serializable {
	private static final long serialVersionUID = 4474090515560273224L;

	@Nonnull private final String mObjectId;
	@Nonnull public String getObjectId() {
		return mObjectId;
	}

	@Nonnull private final Class<?> mObjectClass;
	@Nonnull public Class getObjectClass() {
		return mObjectClass;
	}

	public ProxiedObject(@Nonnull String objectId, @Nonnull Class<?> objectClass) {
		mObjectId = objectId;
		mObjectClass = objectClass;
	}
}
