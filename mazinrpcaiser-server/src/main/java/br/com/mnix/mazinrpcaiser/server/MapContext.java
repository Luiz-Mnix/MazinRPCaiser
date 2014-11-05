package br.com.mnix.mazinrpcaiser.server;

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
	public MapContext(@Nonnull Map<String, Serializable> innerMap) {
		mInnerMap = innerMap;
	}

	@Override
	public void putObject(@Nonnull String objectId, @Nonnull Serializable object) {
		mInnerMap.put(objectId, object);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T extends Serializable> T getObject(@Nonnull String objectId) {
		return (T) mInnerMap.get(objectId);
	}

	@Nullable
	@Override
	public Serializable getSerializable(@Nonnull String objectId) {
		return mInnerMap.get(objectId);
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
