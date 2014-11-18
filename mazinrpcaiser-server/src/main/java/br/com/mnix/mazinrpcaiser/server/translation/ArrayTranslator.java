package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.server.data.IContext;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * Created by mnix05 on 11/17/14.
 *
 * @author mnix05
 */

public class ArrayTranslator implements ITranslator {
	@Nonnull
	@Override
	public Serializable translate(@Nonnull Serializable data, @Nonnull IContext context) throws TranslationException {
		Class componentType = data.getClass().getComponentType();

		if(componentType.isPrimitive()) {
			int length =  Array.getLength(data);
			Object translated = Array.newInstance(data.getClass().getComponentType(), length);

			for(int idx = 0; idx < length; ++idx) {
				Array.set(translated, idx, Array.get(data, idx));
			}

			return (Serializable) translated;
		}

		try {
			Serializable[] collection = (Serializable[]) data;
			Serializable[] bufferCollection = new Serializable[collection.length];
			Class<?> translatedArrayComponentClass = Object.class;
			int idx = 0;

			for(Serializable obj : collection) {
				bufferCollection[idx] = DataTranslator.translateData(obj, context);
				if(bufferCollection[idx] != null) {
					translatedArrayComponentClass = bufferCollection[idx].getClass();
				}

				++idx;
			}

			Serializable[] fixedCollection =  (Serializable[]) Array.newInstance(
					translatedArrayComponentClass,
					collection.length
			);

			int bufferIdx = 0;
			for (Serializable translated : bufferCollection) {
				fixedCollection[bufferIdx++] = translated;
			}

			return fixedCollection;
		} catch (ClassCastException e) {
			throw new TranslationException(e);
		}
	}
}
