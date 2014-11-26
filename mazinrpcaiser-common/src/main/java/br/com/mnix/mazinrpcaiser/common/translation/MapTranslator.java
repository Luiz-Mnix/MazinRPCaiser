package br.com.mnix.mazinrpcaiser.common.translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by mnix05 on 11/11/14.
 *
 * @author mnix05
 */
@Translator(of = Map.class)
public class MapTranslator implements ITranslator {
	@Nonnull
	@Override
	public Object translate(@Nonnull Object data, @Nullable ITranslator fallback) throws TranslationException {
		try {
			Map map = (Map) data;

			Map fixedMap;
			fixedMap = map.getClass().getConstructor().newInstance();

			for(Object entryObj : map.entrySet()) {
				Map.Entry entry = (Map.Entry) entryObj;
				Object fixedKey = DataTranslator.translateData(entry.getKey(), fallback);
				Object fixedValue = DataTranslator.translateData(entry.getValue(), fallback);
				//noinspection unchecked
				fixedMap.put(fixedKey, fixedValue);
			}

			return fixedMap;

		} catch (InstantiationException | NoSuchMethodException | InvocationTargetException
				| IllegalAccessException | ClassCastException e) {
			throw new TranslationException(e);
		}
	}
}
