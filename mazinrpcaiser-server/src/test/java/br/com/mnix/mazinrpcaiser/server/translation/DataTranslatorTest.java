package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.common.NotTransmissibleObject;
import br.com.mnix.mazinrpcaiser.common.Transmissible;
import br.com.mnix.mazinrpcaiser.server.data.IContext;
import br.com.mnix.mazinrpcaiser.server.data.MapContext;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;

public class DataTranslatorTest {
	@Test
	public void testTranslateData_TransmissibleData() throws Exception {
		// Arrange
		final Map<String, Serializable> contextMap = new HashMap<>();
		final String id = "foo";
		final IContext context = new MapContext(id, contextMap);
		final TransmissibleStub stub = new TransmissibleStub();

		// Act
		final Serializable translated = DataTranslator.translateData(stub, context);

		// Assert
		assertSame(stub, translated);
	}

	@Test
	public void testTranslateData_CustomTranslator() throws Exception {
		// Arrange
		final Map<String, Serializable> contextMap = new HashMap<>();
		final String id = "foo";
		final IContext context = new MapContext(id, contextMap);
		final TranslatedStub stub = new TranslatedStub(id+id);

		// Act
		final Serializable translated = DataTranslator.translateData(stub, context);

		// Assert
		assertEquals(id + id, translated);
	}
	@Test
	public void testTranslateData_CustomTranslatorInheritance() throws Exception {
		// Arrange
		final Map<String, Serializable> contextMap = new HashMap<>();
		final String id = "foo";
		final IContext context = new MapContext(id, contextMap);
		final TranslatedStub stub = new SecondTranslatedStub();

		// Act
		final Serializable translated = DataTranslator.translateData(stub, context);

		// Assert
		assertEquals(SecondTranslatedStub.STRING, translated);
	}

	@Test
	public void testTranslateData_NoncommunicableData() throws Exception {
		// Arrange
		final Map<String, Serializable> contextMap = new HashMap<>();
		final String id = "foo";
		final IContext context = new MapContext(id, contextMap);
		final NoncommunicableStub stub = new NoncommunicableStub();

		// Act
		final Serializable translated = DataTranslator.translateData(stub, context);

		// Assert
		assert translated != null;
		assertEquals(NotTransmissibleObject.class, translated.getClass());
	}

	@Test
	public void testTranslateData_CollectionData() throws Exception {
		// Arrange
		final Map<String, Serializable> contextMap = new HashMap<>();
		final String id = "foo";
		final IContext context = new MapContext(id, contextMap);
		final Collection<TransmissibleStub> collection = new ArrayList<>();

		// Act
		collection.add(new TransmissibleStub());
		collection.add(new TransmissibleStub());
		collection.add(new TransmissibleStub());
		final Serializable translated = DataTranslator.translateData((Serializable) collection, context);

		// Assert
		assert translated != null;
		assertEquals(collection.getClass(), translated.getClass());
		assertEquals(collection.size(), ((Collection)translated).size());
	}

	@Test
	public void testTranslateData_MapData() throws Exception {
		// Arrange
		final Map<String, Serializable> contextMap = new HashMap<>();
		final String id = "foo";
		final IContext context = new MapContext(id, contextMap);
		final Map<String, TransmissibleStub> map = new HashMap<>();

		// Act
		map.put(id, new TransmissibleStub());
		map.put(id + id, new TransmissibleStub());
		map.put(id + id + id, new TransmissibleStub());
		final Serializable translated = DataTranslator.translateData((Serializable) map, context);

		// Assert
		assert translated != null;
		assertEquals(map.getClass(), translated.getClass());
		assertEquals(map.size(), ((Map) translated).size());
	}

	@Test
	public void testTranslateData_CustomArrayTranslator() throws Exception {
		// Arrange
		final Map<String, Serializable> contextMap = new HashMap<>();
		final String id = "foo";
		final IContext context = new MapContext(id, contextMap);
		final ArrayStub[] array = { new ArrayStub() };

		// Act
		final Serializable translated = DataTranslator.translateData(array, context);

		// Assert
		assertEquals(Arrays.hashCode(array), translated);
	}

	@Test
	public void testTranslateData_PrimitiveType() throws Exception {
		// Arrange
		final Map<String, Serializable> contextMap = new HashMap<>();
		final String id = "foo";
		final IContext context = new MapContext(id, contextMap);
		final int data = 2;

		// Act
		final Serializable translated = DataTranslator.translateData(data, context);

		// Assert
		assert translated != null;
		assertEquals(Integer.class, translated.getClass());
		assertEquals(data, translated);
	}

	@Test
	public void testTranslateData_FindTranslator_SavedInstances() throws Exception {
		// Arrange
		final Method findMethod = DataTranslator.class.getDeclaredMethod("findTranslator", Class.class);
		final Field translatorsField = DataTranslator.class.getDeclaredField("sTranslators");
		final Class[] baseClasses = {
				Number.class, Character.class, String.class, Class.class, Exception.class, Collection.class,
				Map.class, TranslatedStub.class
		};
		final Class[] derivedClasses = {
				Float.class, Integer.class, Double.class, IllegalArgumentException.class, ArrayList.class,
				HashMap.class, SecondTranslatedStub.class
		};

		// Act
		findMethod.setAccessible(true);
		translatorsField.setAccessible(true);
		//noinspection unchecked
		final Map<Class<?>, ITranslator> translators = (Map<Class<?>, ITranslator>) translatorsField.get(null);
		final int translatorCount = translators.size();

		// Assert
		for (Class clazz : baseClasses) {
			assertNotEquals(ProxiedObjectTranslator.class, findMethod.invoke(null, clazz));
			assertEquals(translatorCount, translators.size());
		}
		for(Class clazz : derivedClasses) {
			findMethod.invoke(null, clazz);
			assertEquals(translatorCount, translators.size());
		}
	}

	@Transmissible
	public static class TransmissibleStub implements Serializable {
		private static final long serialVersionUID = -652799295024296850L;
	}

	public static class NoncommunicableStub implements Serializable {
		private static final long serialVersionUID = -7478235553172876197L;
	}

	public static class TranslatedStub implements Serializable {
		private static final long serialVersionUID = -3412481930065097439L;
		@Nonnull private final String mFoo;
		public TranslatedStub(@Nonnull String foo) {
			mFoo = foo;
		}
		@Nonnull public String getFoo() {
			return mFoo;
		}
	}

	public static class SecondTranslatedStub extends TranslatedStub {
		public static final String STRING = "bar";
		private static final long serialVersionUID = 1602487105010592423L;
		public SecondTranslatedStub() {
			super(STRING);
		}
	}

	@Translator(of = TranslatedStub.class)
	public static class TranslatedStubTranslator implements ITranslator {
		@Nonnull
		@Override
		public Serializable translate(@Nonnull Serializable data, @Nonnull IContext context)
				throws TranslationException {
			return ((TranslatedStub)data).getFoo();
		}
	}

	@Transmissible
	public static class ArrayStub implements Serializable {
		private static final long serialVersionUID = -6106798904369945521L;
	}
	@Translator(of = ArrayStub[].class)
	public static class ArrayStubTranslator implements ITranslator {
		@Nonnull
		@Override
		public Serializable translate(@Nonnull Serializable data, @Nonnull IContext context)
				throws TranslationException {
			return Arrays.hashCode((Object[]) data);
		}
	}
}