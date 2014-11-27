package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.DefaultImplementation;
import br.com.mnix.mazinrpcaiser.common.DistributedVersion;
import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;
import br.com.mnix.mazinrpcaiser.common.exception.InterfaceHasNoDefaultImplementationException;
import br.com.mnix.mazinrpcaiser.common.request.CreateObjectRequest;
import br.com.mnix.mazinrpcaiser.server.data.IContext;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import br.com.mnix.mazinrpcaiser.server.translation.ServerDataTranslator;
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
		Class distributedInterfaceClass = request.getDistributedInterface();
		DistributedVersion distributedVersion =
				(DistributedVersion) distributedInterfaceClass.getAnnotation(DistributedVersion.class);
		Class backendInterfaceClass = distributedVersion.of();
		Class implementationClass = request.getImplementationClass();

		if(!request.getOverwrites() && context.containsObjectId(request.getObjectId())) {
			Serializable obj = context.getSerializable(request.getObjectId());
			Class expectedObjClass = implementationClass != null ? implementationClass : backendInterfaceClass;

			if(expectedObjClass.isInstance(obj)) {
				return null;
			}

			throw new IllegalArgumentException("A different object with this ID already exists!");
		}

		if(implementationClass == null) {
			implementationClass = getDefaultImplementation(backendInterfaceClass);
		}

		Serializable[] args;
		try {
			args = (Serializable[]) ServerDataTranslator.decode(request.getInitializationArgs(), context);
		} catch(ClassCastException ignored) {
			args = null;
		}
		Serializable obj = createObject(implementationClass, args);
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
