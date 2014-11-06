package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.MethodData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by mnix05 on 11/5/14.
 *
 * @author mnix05
 */
public class MethodHandler extends DefaultDataHandler<MethodData> {
	public MethodHandler() {
		super(MethodData.class);
	}

	@Nullable
	@Override
	protected Object processActionForReal(@Nonnull MethodData actionData, @Nonnull IContext context,
										  @Nonnull IDataGrid dataGrid) throws Exception {
		Class<?>[] argsClasses = ObjectUtils.getTypesOfObjects(actionData.getArgs());
		Serializable obj = context.getSerializable(actionData.getObjectId());
		Method method = obj.getClass().getMethod(actionData.getMethodName(), argsClasses);

		try {
			return method.invoke(obj, actionData.getArgs());
		} catch (InvocationTargetException e) {
			throw (Exception) e.getCause();
		}
	}
}
