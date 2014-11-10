package br.com.mnix.mazinrpcaiser.common.request;

import java.io.Serializable;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public final class RequestUtils {
	private RequestUtils() {}

	public static String getRequestGroup(Serializable actionData) {
		return getRequestGroup(actionData.getClass());
	}

	public static String getRequestGroup(Class<? extends Serializable> actionDataClass) {
		return actionDataClass.getCanonicalName() + "_receiver";
	}
}
