package br.com.mnix.mazinrpcaiser.server.data;

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
	// --Commented out by Inspection (11/10/14, 3:44 PM):@Nonnull <T extends Serializable> T getObject(@Nonnull String objectId) throws ObjectNotFoundException;
	@Nonnull Serializable getSerializable(@Nonnull String objectId) throws ObjectNotFoundException;
	boolean containsObjectId(@Nonnull String objectId);
	@Nonnull String getObjectId(@Nonnull Serializable object) throws ObjectNotFoundException;
	boolean containsObject(@Nonnull Serializable object);
	int size();
}
