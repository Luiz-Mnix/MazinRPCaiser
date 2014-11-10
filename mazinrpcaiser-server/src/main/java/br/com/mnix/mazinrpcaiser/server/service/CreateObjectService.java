package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.*;
import br.com.mnix.mazinrpcaiser.common.exception.InterfaceDoesNotExistException;
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

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	protected Object processRequestImpl(@Nonnull CreateObjectRequest actionData, @Nonnull IContext context,
										@Nonnull IDataGrid dataGrid) throws Exception {
		Class serviceClass = actionData.getServiceClass();

		if(serviceClass.isAnnotationPresent(DistributedVersion.class)) {
			DistributedVersion distributedVersion = (DistributedVersion) serviceClass.getAnnotation(
					DistributedVersion.class
			);
			Class backendClass = distributedVersion.of();
			Set<Class<?>> implementationClasses = new Reflections(MazinRPCaiserConstants.DEFAULT_USER_PACKAGE)
					.getSubTypesOf(backendClass);

			for(Class<?> implementationClass : implementationClasses) {
				if(implementationClass.isAnnotationPresent(DefaultImplementation.class)) {
					Class<?>[] argsClasses = ObjectUtils.getTypesOfObjects(actionData.getInitializationArgs());
					Constructor constructor = implementationClass.getConstructor(argsClasses);
					Object obj;
					try {
						obj = constructor.newInstance((Object[]) actionData.getInitializationArgs());
					} catch (InvocationTargetException e) {
						throw (Exception) e.getCause();
					}
					context.putObject(actionData.getObjectId(), (Serializable) obj);
					return null;
				}
			}

			throw new InterfaceHasNoDefaultImplementationException();
		}

		throw new InterfaceDoesNotExistException();
	}
}
