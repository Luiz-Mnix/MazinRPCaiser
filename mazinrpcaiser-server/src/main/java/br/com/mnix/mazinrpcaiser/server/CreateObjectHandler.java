package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.CreateObjectData;
import br.com.mnix.mazinrpcaiser.common.DefaultImplementation;
import br.com.mnix.mazinrpcaiser.common.DistributedVersion;
import br.com.mnix.mazinrpcaiser.common.exception.ServiceDoesNotExistException;
import br.com.mnix.mazinrpcaiser.common.exception.ServiceHasNoDefaultImplementationException;
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
public class CreateObjectHandler extends DefaultDataHandler<CreateObjectData> {
	protected CreateObjectHandler() {
		super(CreateObjectData.class);
	}

	@Nullable
	@Override
	protected Object processActionForReal(@Nonnull CreateObjectData actionData, @Nonnull IContext context,
										  @Nonnull IDataGrid dataGrid) throws Exception {
		Class serviceClass = actionData.getServiceClass();

		if(serviceClass.isAnnotationPresent(DistributedVersion.class)) {
			DistributedVersion distributedVersion = (DistributedVersion) serviceClass.getAnnotation(
					DistributedVersion.class
			);
			Class backendClass = distributedVersion.of();
			Set<Class<?>> implementationClasses = new Reflections("br.com").getSubTypesOf(backendClass);

			for(Class<?> implementationClass : implementationClasses) {
				if(implementationClass.isAnnotationPresent(DefaultImplementation.class)) {
					Class<?>[] argsClasses = ObjectUtils.getTypesOfObjects(actionData.getInitializationArgs());
					Constructor constructor = implementationClass.getConstructor(argsClasses);
					Object obj;
					try {
						obj = constructor.newInstance(actionData.getInitializationArgs());
					} catch (InvocationTargetException e) {
						throw (Exception) e.getCause();
					}
					context.putObject(actionData.getObjectId(), (Serializable) obj);
					return null;
				}
			}

			throw new ServiceHasNoDefaultImplementationException();
		}

		throw new ServiceDoesNotExistException();
	}
}
