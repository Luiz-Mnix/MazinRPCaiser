package br.com.mnix.mazinrpcaiser.common.request;

import br.com.mnix.mazinrpcaiser.common.DistributedObjectUtils;

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

	public CreateObjectRequest(@Nonnull String objectId, boolean overwrites, @Nonnull Class distributedInterface,
							   @Nullable Class implementationClass, @Nullable Serializable... initializationArgs) {
		super(objectId);

		DistributedObjectUtils.assertDistributedType(distributedInterface, implementationClass);

		mOverwrites = overwrites;
		mDistributedInterface = distributedInterface;
		mImplementationClass = implementationClass;
		mInitializationArgs = initializationArgs != null ?
				Arrays.copyOf(initializationArgs, initializationArgs.length) : null;
	}

	public CreateObjectRequest(@Nonnull String objectId, boolean overwrites, @Nonnull Class distributedInterface,
							   @Nullable Serializable... initializationArgs) {
		this(objectId, overwrites, distributedInterface, null, initializationArgs);
	}

	private final boolean mOverwrites;
	public boolean getOverwrites() {
		return mOverwrites;
	}

	@Nonnull private final Class mDistributedInterface;
	@Nonnull public Class getDistributedInterface() {
		return mDistributedInterface;
	}

	@Nullable private final Class mImplementationClass;
	@Nullable public Class getImplementationClass() {
		return mImplementationClass;
	}

	@Nullable private final Serializable[] mInitializationArgs;
	@Nullable public Serializable[] getInitializationArgs() {
		return mInitializationArgs != null ? Arrays.copyOf(mInitializationArgs, mInitializationArgs.length) : null;
	}
}
