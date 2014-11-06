package br.com.mnix.mazinrpcaiser.server;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * CreNonnull mnix05 on 11/5/14.
 *
 * @author mnix05
 */
public final class ObjectUtils {
	private ObjectUtils() {}

	@Nullable
	public static Class<?>[] getTypesOfObjects(@Nullable Object[] objects) {
		if(objects == null) {
			return null;
		}

		Collection<Class<?>> classes = new ArrayList<>();

		for (Object obj : objects) {
			classes.add(obj == null ? null : obj.getClass());
		}

		return classes.toArray(new Class<?>[classes.size()]);
	}
}
