package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.request.MethodRequest;
import br.com.mnix.mazinrpcaiser.server.data.IContext;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import br.com.mnix.mazinrpcaiser.server.translation.ServerDataTranslator;

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
@Service(forRequest = MethodRequest.class)
public class MethodService extends DefaultService<MethodRequest> {
	public MethodService() {
		super(MethodRequest.class);
	}

	@Nullable
	@Override
	protected Serializable processRequestImpl(@Nonnull MethodRequest request, @Nonnull IContext context,
											  @Nonnull IDataGrid dataGrid) throws Throwable {
		Serializable[] args = (Serializable[]) ServerDataTranslator.decode(request.getArgs(), context);
		Class<?>[] argsClasses = ObjectUtils.getTypesOfObjects(args);
		Serializable obj = context.getSerializable(request.getObjectId());
		Method method = obj.getClass().getDeclaredMethod(request.getMethodName(), argsClasses);

		try {
			Object processed = method.invoke(obj, (Object[]) args);
			context.putObject(request.getObjectId(), obj);
			return (Serializable) processed;
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}
}
