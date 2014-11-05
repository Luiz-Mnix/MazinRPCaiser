package br.com.mnix.mazinrpcaiser.common;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
@InputActionData
public class OpenSessionData implements IReturnVoid {
	private static final long serialVersionUID = -5448964193585558040L;

	private final boolean mOverwritesExisting;

	public OpenSessionData(boolean overwritesExisting) {
		mOverwritesExisting = overwritesExisting;
	}

	public boolean getOverwritesExisting() {
		return mOverwritesExisting;
	}
}
