package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;
import org.reflections.Reflections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public final class ServiceFactory {
	private ServiceFactory() {}

	@SuppressWarnings("unchecked")
	@Nonnull public static IService getServiceForRequest(@Nonnull Class<? extends Serializable> requestClass)
			throws RequestHasNoServiceException {

		IService service;

		if((service = getServiceFromAnnotations(requestClass)) != null) {
			return service;

		} else {
			Class<?> superClass = requestClass.getSuperclass();

			if(Serializable.class.isAssignableFrom(superClass)) {
				return getServiceForRequest((Class<? extends Serializable>) superClass);
			} else {
				throw new RequestHasNoServiceException(String.format(
						"Data Type %s has no action handler with errorless default constructor",
						requestClass.getCanonicalName())
				);
			}
		}
	}

	@Nullable
	private static IService getServiceFromAnnotations(@Nonnull Class<? extends Serializable> requestClass) {
		Reflections reflections = new Reflections(MazinRPCaiserConstants.DEFAULT_USER_PACKAGE);

		for (Class<?> serviceClass : reflections.getTypesAnnotatedWith(Service.class)) {
			if(IService.class.isAssignableFrom(serviceClass)) {

				Service annotation = serviceClass.getAnnotation(Service.class);

				if(annotation.forRequest().equals(requestClass)) {
					try {
						return (IService) serviceClass.getConstructor().newInstance();
					} catch (InstantiationException | IllegalAccessException | InvocationTargetException
							| NoSuchMethodException ignored)
					{}
					break;
				}
			}
		}

		return null;
	}
}
