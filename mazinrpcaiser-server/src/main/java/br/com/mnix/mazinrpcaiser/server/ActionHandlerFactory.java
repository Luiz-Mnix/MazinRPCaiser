package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.IActionData;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public final class ActionHandlerFactory {
	private ActionHandlerFactory() {}
	public static IActionHandler handlerForActionDataType(@Nonnull Class<? extends IActionData> actionDataType) {
		int nameComplementPosition = actionDataType.getName().lastIndexOf("Data");

		if(nameComplementPosition > -1) {
			try {
				Class<?> handlerClass = Class.forName(
						actionDataType.getName().substring(0, nameComplementPosition) + "Handler"
				);
				return (IActionHandler) handlerClass.getConstructor().newInstance();
			} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
					| InvocationTargetException ignored) {}
		}

		//TODO
		//FUCK
		return null;
	}
}
