package br.com.mnix.mazinrpcaiser.common.translation;

import br.com.mnix.mazinrpcaiser.common.MazinRPCaiserConstants;
import br.com.mnix.mazinrpcaiser.common.ITransmissible;
import org.reflections.Reflections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by mnix05 on 11/10/14.
 *
 * @author mnix05
 */
public final class DataTranslator {
	private DataTranslator() {}

	@Nonnull private static final ITranslator DEFAULT_ARRAY_TRANSLATOR = new ArrayTranslator();

	@Nonnull private static final Map<Class<?>, ITranslator> sTranslators = new HashMap<>();
	static {
		Reflections reflections = new Reflections(MazinRPCaiserConstants.DEFAULT_USER_PACKAGE);
		Set<Class<?>> translatorsClasses = reflections.getTypesAnnotatedWith(Translator.class);

		for (Class<?> translatorClass : translatorsClasses) {
			if(ITranslator.class.isAssignableFrom(translatorClass)) {
				Class<?>[] supportedClasses = translatorClass.getAnnotation(Translator.class).of();

				try {
					for (Class<?> supportedClass : supportedClasses) {
						sTranslators.put(
								supportedClass,
								(ITranslator) translatorClass.getConstructor().newInstance()
						);
					}
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException
						| NoSuchMethodException ignored) {
				}
			}
		}
	}

	@Nullable public static Object translateData(@Nullable Object data, @Nullable ITranslator fallback)
			throws TranslationException {
		if(data == null) {
			return null;
		}

		if(data instanceof ITransmissible) {
			return data;
		}

		ITranslator translator = findTranslator(data.getClass());
		ITranslator trueFallback;

		if(translator == null) {
			if(fallback == null) {
				throw new TranslatorNotFoundException(data.getClass());
			} else {
				translator = fallback;
				trueFallback = fallback;
			}
		} else {
			trueFallback = fallback;
		}

		return translator.translate(data, trueFallback);
	}

	@Nullable private static ITranslator findTranslator(Class forClass) {
		for(Class<?> translatedClass = forClass;
			!translatedClass.equals(Object.class);
			translatedClass = translatedClass.getSuperclass()) {

			if(sTranslators.containsKey(translatedClass)) {
				return sTranslators.get(translatedClass);
			}

			for (Class<?> interfaceClass : translatedClass.getInterfaces()) {
				if(sTranslators.containsKey(interfaceClass)) {
					return sTranslators.get(interfaceClass);
				}
			}
		}

		if(forClass.isArray()) {
			return DEFAULT_ARRAY_TRANSLATOR;
		}

		return null;
	}
}
