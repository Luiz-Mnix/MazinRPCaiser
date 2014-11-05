package br.com.mnix.mazinrpcaiser.common;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public final class ActionDataUtils {
	private ActionDataUtils() {}

	public static String getActionType(IActionData actionData) {
		return getActionType(actionData.getClass());
	}

	public static String getActionType(Class<? extends IActionData> actionDataClass) {
		return actionDataClass.getCanonicalName();
	}
}
