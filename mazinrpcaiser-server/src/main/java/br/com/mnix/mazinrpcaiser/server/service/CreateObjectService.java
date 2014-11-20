package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.DefaultImplementation;
import br.com.mnix.mazinrpcaiser.common.DistributedVersion;
import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;
import br.com.mnix.mazinrpcaiser.common.exception.InterfaceHasNoDefaultImplementationException;
import br.com.mnix.mazinrpcaiser.common.request.CreateObjectRequest;
import br.com.mnix.mazinrpcaiser.server.data.IContext;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import org.reflections.Reflections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * Created by mnix05 on 11/5/14.
 *
 * @author mnix05
 */
@Service(forRequest = CreateObjectRequest.class)
public class CreateObjectService extends DefaultService<CreateObjectRequest> {
	public CreateObjectService() {
		super(CreateObjectRequest.class);
	}

	@Nullable
	@Override
	protected Serializable processRequestImpl(@Nonnull CreateObjectRequest request, @Nonnull IContext context,
											  @Nonnull IDataGrid dataGrid) throws Throwable {
		Class implementationClass = request.getImplementationClass();

		if(implementationClass == null) {
			Class distributedInterfaceClass = request.getDistributedInterface();
			DistributedVersion distributedVersion =
					(DistributedVersion) distributedInterfaceClass.getAnnotation(DistributedVersion.class);
			Class backendInterfaceClass = distributedVersion.of();
			implementationClass = getDefaultImplementation(backendInterfaceClass);
		}

		Serializable obj = createObject(implementationClass, request.getInitializationArgs());
		context.putObject(request.getObjectId(), obj);
		return null;
	}

	@Nonnull private static Serializable createObject(@Nonnull Class<?> objClass,
													  @Nullable Serializable[] initArgs) throws Throwable {
		Class<?>[] argsClasses = ObjectUtils.getTypesOfObjects(initArgs);
		Constructor constructor = objClass.getConstructor(argsClasses);

		try {
			return (Serializable) constructor.newInstance((Object[]) initArgs);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	@Nonnull private static Class<?> getDefaultImplementation(@Nonnull Class backendInterfaceClass)
			throws InterfaceHasNoDefaultImplementationException {
		@SuppressWarnings("unchecked") Set<Class<?>> implementationClasses =
				new Reflections(MazinRPCaiserConstants.DEFAULT_USER_PACKAGE).getSubTypesOf(backendInterfaceClass);

		for(Class<?> implementationClass : implementationClasses) {
			if(implementationClass.isAnnotationPresent(DefaultImplementation.class)
					&& Serializable.class.isAssignableFrom(implementationClass)) {
				return implementationClass;
			}
		}

		throw new InterfaceHasNoDefaultImplementationException();
	}
}
