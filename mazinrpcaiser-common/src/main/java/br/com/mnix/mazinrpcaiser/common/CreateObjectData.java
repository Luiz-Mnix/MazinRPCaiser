package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
@InputActionData
public class CreateObjectData extends DefaultObjectActionData {
	private static final long serialVersionUID = -8998716644568493004L;

	protected CreateObjectData(@Nonnull String objectId, @Nonnull Class serviceClass) {
		super(objectId);
		mServiceClass = serviceClass;
	}

	@Nonnull private final Class mServiceClass;
	@Nonnull public Class getServiceClass() {
		return mServiceClass;
	}
}
