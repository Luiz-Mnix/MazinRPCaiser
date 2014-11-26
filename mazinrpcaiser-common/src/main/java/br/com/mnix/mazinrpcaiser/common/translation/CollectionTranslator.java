package br.com.mnix.mazinrpcaiser.common.translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Created by mnix05 on 11/11/14.
 *
 * @author mnix05
 */
@Translator(of = Collection.class)
public class CollectionTranslator implements ITranslator {
	@Nonnull
	@Override
	public Object translate(@Nonnull Object data, @Nullable ITranslator fallback)
			throws TranslationException {
		try {
			Collection collection = (Collection) data;

			Collection fixedCollection;
			try {
				fixedCollection = collection.getClass().getConstructor().newInstance();
			} catch(NoSuchMethodException ignored) {
				fixedCollection = collection.getClass().getConstructor(Integer.TYPE).newInstance(collection.size());
			}

			for (Object obj : collection) {
				//noinspection unchecked
				fixedCollection.add(DataTranslator.translateData(obj, fallback));
			}

			return fixedCollection;

		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
				| InvocationTargetException | ClassCastException e) {
			throw new TranslationException(e);
		}
	}
}
