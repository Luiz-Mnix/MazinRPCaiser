package br.com.mnix.mazinrpcaiser.common.translation;

import br.com.mnix.mazinrpcaiser.common.ITransmissible;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;

public class DataTranslatorTest {
	@Test
	public void testTranslateData_TransmissibleData() throws Exception {
		// Arrange
		final ITransmissibleStub stub = new ITransmissibleStub();

		// Act
		final Object translated = DataTranslator.translateData(stub, null);

		// Assert
		assertSame(stub, translated);
	}

	@Test
	public void testTranslateData_CustomTranslator() throws Exception {
		// Arrange
		final String id = "foo";
		final TranslatedStub stub = new TranslatedStub(id+id);

		// Act
		final Object translated = DataTranslator.translateData(stub, null);

		// Assert
		assertEquals(id + id, translated);
	}
	@Test
	public void testTranslateData_CustomTranslatorInheritance() throws Exception {
		// Arrange
		final TranslatedStub stub = new SecondTranslatedStub();

		// Act
		final Object translated = DataTranslator.translateData(stub, null);

		// Assert
		assertEquals(SecondTranslatedStub.STRING, translated);
	}

	@Test
	public void testTranslateData_NoncommunicableData() throws Exception {
		// Arrange
		final NoncommunicableStub stub = new NoncommunicableStub();

		// Act
		final Object translated = DataTranslator.translateData(stub, new ITranslator() {
			@Nonnull @Override public Object translate(@Nonnull Object data, @Nullable ITranslator fallback) throws TranslationException {
				return "NOPE";
			}
		});

		// Assert
		assert translated != null;
		assertEquals("NOPE", translated);
	}

	@Test
	public void testTranslateData_CollectionData() throws Exception {
		// Arrange
		final Collection<ITransmissibleStub> collection = new ArrayList<>();

		// Act
		collection.add(new ITransmissibleStub());
		collection.add(new ITransmissibleStub());
		collection.add(new ITransmissibleStub());
		final Object translated = DataTranslator.translateData(collection, null);

		// Assert
		assert translated != null;
		assertEquals(collection.getClass(), translated.getClass());
		assertEquals(collection.size(), ((Collection)translated).size());
	}

	@Test
	public void testTranslateData_MapData() throws Exception {
		// Arrange
		final String id = "foo";
		final Map<String, ITransmissibleStub> map = new HashMap<>();

		// Act
		map.put(id, new ITransmissibleStub());
		map.put(id + id, new ITransmissibleStub());
		map.put(id + id + id, new ITransmissibleStub());
		final Object translated = DataTranslator.translateData(map, null);

		// Assert
		assert translated != null;
		assertEquals(map.getClass(), translated.getClass());
		assertEquals(map.size(), ((Map) translated).size());
	}

	@Test
	public void testTranslateData_CustomArrayTranslator() throws Exception {
		// Arrange
		final ArrayStub[] array = { new ArrayStub() };

		// Act
		final Object translated = DataTranslator.translateData(array, null);

		// Assert
		assertEquals(Arrays.hashCode(array), translated);
	}

	@Test
	public void testTranslateData_ArrayTranslator() throws Exception {
		// Arrange
		final int[] array = { 1, 2, 3 };

		// Act
		final Object translated = DataTranslator.translateData(array, null);

		// Assert
		assert translated != null;
		assertTrue(translated.getClass().isArray());
		assertEquals(array.length, Array.getLength(translated));
		for(int idx = 0; idx < array.length; ++idx) {
			assertEquals(array[idx], Array.get(translated, idx));
		}
	}

	@Test(expected = TranslatorNotFoundException.class)
	public void testTranslateData_TranslatorNotFound() throws Exception {
		// Arrange
		final Object obj = new Runnable() {
			@Override public void run() {}
		};

		// Act & Assert
		DataTranslator.translateData(obj, null);
	}

	@Test
	public void testTranslateData_PrimitiveType() throws Exception {
		// Arrange
		final int data = 2;

		// Act
		final Object translated = DataTranslator.translateData(data, null);

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
			findMethod.invoke(null, clazz);
			assertEquals(translatorCount, translators.size());
		}
		for(Class clazz : derivedClasses) {
			findMethod.invoke(null, clazz);
			assertEquals(translatorCount, translators.size());
		}
	}

	public static class ITransmissibleStub implements ITransmissible {
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
		public Object translate(@Nonnull Object data, @Nullable ITranslator fallback)
				throws TranslationException {
			return ((TranslatedStub)data).getFoo();
		}
	}

	public static class ArrayStub implements ITransmissible {
		private static final long serialVersionUID = -6106798904369945521L;
	}
	@Translator(of = ArrayStub[].class)
	public static class ArrayStubTranslator implements ITranslator {
		@Nonnull
		@Override
		public Object translate(@Nonnull Object data, @Nullable ITranslator fallback)
				throws TranslationException {
			return Arrays.hashCode((Object[]) data);
		}
	}
}