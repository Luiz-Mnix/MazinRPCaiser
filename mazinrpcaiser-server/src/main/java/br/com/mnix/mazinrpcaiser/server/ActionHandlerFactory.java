package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.IActionData;
import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;
import org.reflections.Reflections;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public final class ActionHandlerFactory {
	private ActionHandlerFactory() {}

	@Nonnull private static final String DEFAULT_HANDLER_SUFFIX = "Handler";

	@SuppressWarnings("unchecked")
	@Nonnull public static IActionHandler handlerForActionDataType(@Nonnull Class<? extends IActionData> actionDataType)
			throws DataTypeHasNoHandlerException {

		Reflections reflections = new Reflections(MazinRPCaiserConstants.DEFAULT_USER_PACKAGE);

		for (Class<? extends IActionHandler> handlerClass : reflections.getSubTypesOf(IActionHandler.class)) {
			if(handlerClass.isAnnotationPresent(ActionHandler.class)) {
				ActionHandler annotation = handlerClass.getAnnotation(ActionHandler.class);
				if(annotation.to().equals(actionDataType)) {
					try {
						return handlerClass.getConstructor().newInstance();
					} catch (InstantiationException | IllegalAccessException | InvocationTargetException
							| NoSuchMethodException ignored)
					{}
					break;
				}
			}
		}

		try {
			Class<?> handlerClass = Class.forName(actionDataType.getName() + DEFAULT_HANDLER_SUFFIX);
			if(IActionHandler.class.isAssignableFrom(handlerClass)) {
				return (IActionHandler) handlerClass.getConstructor().newInstance();
			}
		} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
				| InvocationTargetException ignored) {}

		Class<?> superClass = actionDataType.getSuperclass();

		if(IActionData.class.isAssignableFrom(superClass)) {
			return handlerForActionDataType((Class<? extends IActionData>) superClass);
		} else {
			throw new DataTypeHasNoHandlerException(String.format(
					"Data Type %s has no action handler with errorless default constructor",
					actionDataType.getCanonicalName())
			);
		}
	}
}
