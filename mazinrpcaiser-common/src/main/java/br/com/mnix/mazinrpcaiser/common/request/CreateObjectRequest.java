package br.com.mnix.mazinrpcaiser.common.request;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
@Request
public class CreateObjectRequest extends DefaultObjectRequest implements IReturnVoid {
	private static final long serialVersionUID = -8998716644568493004L;

	public CreateObjectRequest(@Nonnull String objectId, @Nonnull Class serviceClass,
							   @Nullable Serializable[] initializationArgs) {
		super(objectId);
		mServiceClass = serviceClass;
		mInitializationArgs = initializationArgs != null ?
				Arrays.copyOf(initializationArgs, initializationArgs.length) : null;
	}

	@Nonnull private final Class mServiceClass;
	@Nonnull public Class getServiceClass() {
		return mServiceClass;
	}

	@Nullable private final Serializable[] mInitializationArgs;

	@Nullable
	public Serializable[] getInitializationArgs() {
		return mInitializationArgs != null ? Arrays.copyOf(mInitializationArgs, mInitializationArgs.length) : null;
	}
}