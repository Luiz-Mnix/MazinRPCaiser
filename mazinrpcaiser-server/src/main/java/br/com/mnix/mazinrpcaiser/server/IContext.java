package br.com.mnix.mazinrpcaiser.server;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public interface IContext {
	void putObject(@Nonnull String objectId, @Nonnull Serializable object);
	@Nullable <T extends Serializable> T getObject(@Nonnull String objectId);
	@Nullable Serializable getSerializable(@Nonnull String objectId);
	boolean containsObject(@Nonnull String objectId);
	int size();
}
