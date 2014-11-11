package br.com.mnix.mazinrpcaiser.server.data;

import br.com.mnix.mazinrpcaiser.common.exception.ObjectNotFoundException;

import javax.annotation.Nonnull;
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

// --Commented out by Inspection START (11/10/14, 3:44 PM):
//	@Nonnull
//	@SuppressWarnings("unchecked")
//	@Override
//	public <T extends Serializable> T getObject(@Nonnull String objectId) throws ObjectNotFoundException {
//		return (T) getSerializable(objectId);
//	}
// --Commented out by Inspection STOP (11/10/14, 3:44 PM)

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
	public boolean containsObjectId(@Nonnull String objectId) {
		return mInnerMap.containsKey(objectId);
	}

	@Nonnull
	@Override
	public String getObjectId(@Nonnull Serializable object) throws ObjectNotFoundException {
		for (Map.Entry<String, Serializable> entry : mInnerMap.entrySet()) {
			if(object.equals(entry)) {
				return entry.getKey();
			}
		}

		throw new ObjectNotFoundException();
	}

	@Override
	public boolean containsObject(@Nonnull Serializable object) {
		return mInnerMap.containsValue(object);
	}

	@Override
	public int size() {
		return mInnerMap.size();
	}
}
