package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.exception.ObjectNotFoundException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public class MapContext implements IContext {
	@Nonnull private final Map<String, Serializable> mInnerMap;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public MapContext(@Nonnull String id, @Nonnull Map<String, Serializable> innerMap) {
		mId = id;
		mInnerMap = innerMap;
	}

	@Nonnull private final String mId;
	@Nonnull
	@Override
	public String getId() {
		return mId;
	}

	@Override
	public void putObject(@Nonnull String objectId, @Nonnull Serializable object) {
		mInnerMap.put(objectId, object);
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T getObject(@Nonnull String objectId) throws ObjectNotFoundException {
		return (T) getSerializable(objectId);
	}

	@Nonnull
	@Override
	public Serializable getSerializable(@Nonnull String objectId) throws ObjectNotFoundException {
		Serializable obj = mInnerMap.get(objectId);

		if(obj == null) {
			throw new ObjectNotFoundException();
		}

		return obj;
	}

	@Override
	public boolean containsObject(@Nonnull String objectId) {
		return mInnerMap.containsKey(objectId);
	}

	@Override
	public int size() {
		return mInnerMap.size();
	}
}
