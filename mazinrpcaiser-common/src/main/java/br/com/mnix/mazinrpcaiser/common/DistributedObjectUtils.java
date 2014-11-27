package br.com.mnix.mazinrpcaiser.common;

import br.com.mnix.mazinrpcaiser.common.exception.IllegalDistributedTypeException;
import com.google.common.base.Preconditions;
import org.reflections.Reflections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by mnix05 on 11/20/14.
 *
 * @author mnix05
 */
public final class DistributedObjectUtils {
	private DistributedObjectUtils() {}

	public static void assertDistributedType(@Nonnull Class<?> distributedInterface)
			throws IllegalDistributedTypeException {

		if(!distributedInterface.isInterface()) {
			throw new IllegalDistributedTypeException("distributedInterface is not an interface");

		} else if (!distributedInterface.isAnnotationPresent(DistributedVersion.class)) {
			throw new IllegalDistributedTypeException("distributedInterface is not annotated with @DistributedVersion");
		}

		Class<?> backendInterface =
				((DistributedVersion) distributedInterface.getAnnotation(DistributedVersion.class)).of();

		if(!Serializable.class.isAssignableFrom(backendInterface)) {
			throw new IllegalDistributedTypeException("Backend interface does not extend Serializable");
		}

		Method[] backendMethods = backendInterface.getDeclaredMethods();
		for (Method method : distributedInterface.getDeclaredMethods()) {
			for (Method backendMethod : backendMethods) {
				if(method.getName().equals(backendMethod.getName())
						&& method.getParameterTypes().length == backendMethod.getParameterTypes().length) {
					continue;
				}

				throw new IllegalDistributedTypeException(
						"Distributed interface has methods not supported by the backend interface"
				);
			}
		}
	}

	public static void assertDistributedType(@Nonnull Class<?> distributedInterface,
											 @Nullable Class<?> implementationClass)
			throws IllegalDistributedTypeException {
		assertDistributedType(distributedInterface);

		Class<?> backendInterface =
				((DistributedVersion)distributedInterface.getAnnotation(DistributedVersion.class)).of();

		if(implementationClass != null) {
			if(implementationClass.isInterface() || Modifier.isAbstract(implementationClass.getModifiers())) {
				throw new IllegalDistributedTypeException("implementationClass is not a concrete class");
			} else if(!backendInterface.isAssignableFrom(implementationClass)) {
				throw new IllegalDistributedTypeException(
						"implementationClass does not implement the backend interface"
				);
			}
		}
	}

	public static boolean isDistributedMethodMapped(@Nonnull Method method) {
		Preconditions.checkArgument(
				method.getDeclaringClass().isAnnotationPresent(DistributedVersion.class),
				"method does not belong to an interface annotated with @DistributedVersion"
		);

		Class<?> backendInterface =
				((DistributedVersion)method.getDeclaringClass().getAnnotation(DistributedVersion.class)).of();
		String methodName = method.getName();
		Class<?>[] methodArgs = method.getParameterTypes();
		Class<?> methodReturnType = method.getReturnType();

		for (Method backendMethod : backendInterface.getDeclaredMethods()) {
			if(!backendMethod.getName().equals(methodName)) {
				continue;
			}

			Class<?>[] backendMethodArgs = backendMethod.getParameterTypes();

			if(methodArgs.length != backendMethodArgs.length) {
				continue;
			}

			int idx = 0;
			boolean shouldContinue = false;
			for (Class<?> backendMethodArg : backendMethodArgs) {
				if(!isDistributedTypeMapped(backendMethodArg, methodArgs[idx++])) {
					shouldContinue = true;
					break;
				}
			}

			if(shouldContinue) {
				continue;
			}

			if(!isDistributedTypeMapped(backendMethod.getReturnType(), methodReturnType)) {
				continue;
			}

			return true;
		}

		return false;
	}

	public static boolean isDistributedTypeMapped(@Nonnull Class<?> backendType,
												  @Nonnull Class<?> expectedDistributedType) {
		if(backendType.equals(expectedDistributedType)) {
			if(backendType.getCanonicalName().contains("java")) {
				return true;
			}

			if(ITransmissible.class.isAssignableFrom(backendType)) {
				return true;
			}
		} else if(expectedDistributedType.isAnnotationPresent(DistributedVersion.class)) {
			try {
				assertDistributedType(expectedDistributedType);

				if(((DistributedVersion) expectedDistributedType.getAnnotation(DistributedVersion.class)).of().equals(backendType)) {
					return true;
				}
			} catch (IllegalDistributedTypeException ignored) {}
		}

		return false;
	}

	@Nonnull
	public static Class<?> getDistributedVersion(@Nonnull Class<?> backendType) {
		Reflections reflections = new Reflections(MazinRPCaiserConstants.DEFAULT_USER_PACKAGE);

		for(Class<?> distributedType : reflections.getTypesAnnotatedWith(DistributedVersion.class)) {
			Class<?> referencedBackendType =
					((DistributedVersion)distributedType.getAnnotation(DistributedVersion.class)).of();
			if(referencedBackendType.isAssignableFrom(backendType)) {
				return distributedType;
			}
		}

		throw new IllegalArgumentException("backendType has no distributed version");
	}
}
