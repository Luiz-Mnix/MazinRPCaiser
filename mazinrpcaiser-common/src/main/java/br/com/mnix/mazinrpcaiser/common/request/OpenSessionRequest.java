package br.com.mnix.mazinrpcaiser.common.request;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
@Request
public class OpenSessionRequest implements IReturnVoid {
	private static final long serialVersionUID = -5448964193585558040L;

	private final boolean mOverwritesExisting;

	public OpenSessionRequest(boolean overwritesExisting) {
		mOverwritesExisting = overwritesExisting;
	}

	public boolean getOverwritesExisting() {
		return mOverwritesExisting;
	}
}
