package br.com.mnix.mazinrpcaiser.common.request;

import br.com.mnix.mazinrpcaiser.common.DistributedVersion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
@Request
public class CreateObjectRequest extends DefaultObjectRequest implements IReturnVoid {
	private static final long serialVersionUID = -8998716644568493004L;

	public CreateObjectRequest(@Nonnull String objectId, @Nonnull Class distributedInterface,
							   @Nullable Class implementationClass, @Nullable Serializable... initializationArgs) {
		super(objectId);

		validate(distributedInterface, implementationClass);

		mDistributedInterface = distributedInterface;
		mImplementationClass = implementationClass;
		mInitializationArgs = initializationArgs != null ?
				Arrays.copyOf(initializationArgs, initializationArgs.length) : null;
	}

	public CreateObjectRequest(@Nonnull String objectId, @Nonnull Class distributedInterface,
							   @Nullable Serializable... initializationArgs) {
		this(objectId, distributedInterface, null, initializationArgs);
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

	@Nullable
	public Serializable[] getInitializationArgs() {
		return mInitializationArgs != null ? Arrays.copyOf(mInitializationArgs, mInitializationArgs.length) : null;
	}

	public static void validate(@Nonnull Class<?> distributedInterface, @Nullable Class<?> implementationClass)
			throws IllegalArgumentException {

		if(!distributedInterface.isInterface()
				|| !distributedInterface.isAnnotationPresent(DistributedVersion.class)) {
			throw new IllegalArgumentException("distributedInterfaceClass must be an interface" +
					" and annotated with @DistributedVersion");
		}

		Class<?> backendInterface =
				((DistributedVersion) distributedInterface.getAnnotation(DistributedVersion.class)).of();

		if(!Serializable.class.isAssignableFrom(backendInterface)) {
			throw new IllegalArgumentException("Backend interface does not extends Serializable");
		}

		if(implementationClass != null) {
			if(implementationClass.isInterface() ||  Modifier.isAbstract(implementationClass.getModifiers())) {
				throw new IllegalArgumentException("implementationClass must be a concrete class");
			} else if(!backendInterface.isAssignableFrom(implementationClass)) {
				throw new IllegalArgumentException("implementationClass must implement the backend interface");
			}
		}
	}
}
