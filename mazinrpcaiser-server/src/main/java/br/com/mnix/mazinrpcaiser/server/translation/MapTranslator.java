package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.server.data.IContext;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by mnix05 on 11/11/14.
 *
 * @author mnix05
 */
@Translator(of = Map.class)
public class MapTranslator implements ITranslator {
	@Override
	public Serializable translate(Serializable data, IContext context) throws TranslationException {
		try {
			Map map = (Map) data;

			Map fixedMap;
			fixedMap = map.getClass().getConstructor().newInstance();

			for(Object entryObj : map.entrySet()) {
				Map.Entry entry = (Map.Entry) entryObj;
				Object fixedKey = DataTranslator.translateData((Serializable) entry.getKey(), context);
				Object fixedValue = DataTranslator.translateData((Serializable) entry.getValue(), context);
				//noinspection unchecked
				fixedMap.put(fixedKey, fixedValue);
			}

			return (Serializable) fixedMap;

		} catch (InstantiationException | NoSuchMethodException | InvocationTargetException
				| IllegalAccessException | ClassCastException e) {
			throw new TranslationException(e);
		}
	}
}
