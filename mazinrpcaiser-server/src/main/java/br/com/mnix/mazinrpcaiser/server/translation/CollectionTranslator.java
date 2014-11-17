package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.server.data.IContext;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Created by mnix05 on 11/11/14.
 *
 * @author mnix05
 */
@Translator(of = Collection.class)
public class CollectionTranslator implements ITranslator {
	@Override
	public Serializable translate(Serializable data, IContext context) throws TranslationException {
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
				fixedCollection.add(DataTranslator.translateData((Serializable) obj, context));
			}

			return (Serializable) fixedCollection;

		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
				| InvocationTargetException | ClassCastException e) {
			throw new TranslationException(e);
		}
	}
}
