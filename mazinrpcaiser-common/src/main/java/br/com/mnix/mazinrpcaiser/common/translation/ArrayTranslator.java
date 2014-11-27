package br.com.mnix.mazinrpcaiser.common.translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;

/**
 * Created by mnix05 on 11/17/14.
 *
 * @author mnix05
 */

public class ArrayTranslator implements ITranslator {
	@Nonnull
	@Override
	public Object translate(@Nonnull Object data, @Nullable ITranslator fallback)
			throws TranslationException {
		Class componentType = data.getClass().getComponentType();

		if(componentType.isPrimitive()) {
			int length =  Array.getLength(data);
			Object translated = Array.newInstance(data.getClass().getComponentType(), length);

			for(int idx = 0; idx < length; ++idx) {
				Array.set(translated, idx, Array.get(data, idx));
			}

			return translated;
		}

		try {
			Object[] collection = (Object[]) data;
			Object[] bufferCollection = new Object[collection.length];
			Class<?> translatedArrayComponentClass = Object.class;
			int idx = 0;

			for(Object obj : collection) {
				bufferCollection[idx] = DataTranslator.translateData(obj, fallback);
				if(bufferCollection[idx] != null) {
					translatedArrayComponentClass = bufferCollection[idx].getClass();
				}

				++idx;
			}

			Object[] fixedCollection =  (Object[]) Array.newInstance(
					translatedArrayComponentClass,
					collection.length
			);

			int bufferIdx = 0;
			for (Object translated : bufferCollection) {
				fixedCollection[bufferIdx++] = translated;
			}

			return fixedCollection;
		} catch (ClassCastException e) {
			throw new TranslationException(e);
		}
	}
}
