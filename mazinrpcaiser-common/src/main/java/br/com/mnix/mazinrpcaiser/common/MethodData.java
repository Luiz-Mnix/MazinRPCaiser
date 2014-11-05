package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
@InputActionData
public class MethodData extends DefaultObjectActionData implements IReturn<Serializable> {
	private static final long serialVersionUID = 8621393735064551269L;

	@Nonnull private final String mMethodName;
	@Nonnull public String getMethodName() {
		return mMethodName;
	}

	@Nullable private final Serializable[] mParameters;
	@Nullable public Serializable[] getParameters() {
		return mParameters == null ? null : Arrays.copyOf(mParameters, mParameters.length);
	}

	protected MethodData(@Nonnull String objectId, @Nonnull String methodName, @Nullable Serializable[] parameters) {
		super(objectId);
		mMethodName = methodName;
		mParameters = parameters == null ? null : Arrays.copyOf(parameters, parameters.length);
	}
}
