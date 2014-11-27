package br.com.mnix.mazinrpcaiser.common.request;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
@Request
public class CloseSessionRequest implements IReturnVoid {
	private static final long serialVersionUID = 6834118314752803735L;

	private final boolean mWipesContext;
	public boolean getWipesContext() {
		return mWipesContext;
	}

	public CloseSessionRequest(boolean wipesContext) {
		mWipesContext = wipesContext;
	}
}
