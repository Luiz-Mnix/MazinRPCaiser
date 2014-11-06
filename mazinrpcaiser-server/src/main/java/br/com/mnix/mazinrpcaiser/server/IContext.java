package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.exception.ObjectNotFoundException;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public interface IContext {
	@Nonnull String getId();
	void putObject(@Nonnull String objectId, @Nonnull Serializable object);
	@Nonnull <T extends Serializable> T getObject(@Nonnull String objectId) throws ObjectNotFoundException;
	@Nonnull Serializable getSerializable(@Nonnull String objectId) throws ObjectNotFoundException;
	boolean containsObject(@Nonnull String objectId);
	int size();
}
