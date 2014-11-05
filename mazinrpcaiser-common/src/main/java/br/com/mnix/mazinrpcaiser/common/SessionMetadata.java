package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public class SessionMetadata implements Serializable {
	private static final long serialVersionUID = 6716778116178211799L;

	@Nonnull private final String mContextId;
	@Nonnull public String getContextId() {
		return mContextId;
	}

	@Nonnull private final String mServerAddress;
	@Nonnull
	public String getServerAddress() {
		return mServerAddress;
	}

	@Nonnull private final Date mOpenedDate;
	@Nonnull public Date getOpenedDate() {
		return (Date) mOpenedDate.clone();
	}

	public SessionMetadata(@Nonnull String contextId, @Nonnull String serverAddress) {
		mContextId = contextId;
		mServerAddress = serverAddress;
		mOpenedDate = new Date();
	}
}
