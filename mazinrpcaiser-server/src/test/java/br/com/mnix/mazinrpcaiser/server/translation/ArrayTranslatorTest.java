package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.server.data.IContext;
import br.com.mnix.mazinrpcaiser.server.data.MapContext;
import org.junit.Test;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ArrayTranslatorTest {

	@Test
	public void testTranslate_SerializableComponentClass() throws Exception {
		// Arrange
		final String[] data = { "foo", "bar", "bor" };
		final Map<String, Serializable> map = new HashMap<>();
		final IContext context = new MapContext("foo", map);
		final ITranslator translator = new ArrayTranslator();

		// Act
		Object[] translated = (Object[]) translator.translate(data, context);

		// Assert
		assertEquals(data.getClass().getComponentType(), translated.getClass().getComponentType());
		assertEquals(data.getClass(), translated.getClass());
		assertEquals(data.length, translated.length);
		int idx = 0;
		for (Object obj : translated) {
			assertEquals(data[idx++], obj);
		}
	}

	@Test
	public void testTranslate_PrimitiveComponentClass() throws Exception {
		// Arrange
		final int[] data = { 2, 3, 4 };
		final Map<String, Serializable> map = new HashMap<>();
		final IContext context = new MapContext("foo", map);
		final ITranslator translator = new ArrayTranslator();

		// Act
		Serializable translated = translator.translate(data, context);

		// Assert
		int length = Array.getLength(translated);
		assertEquals(data.length, length);
		for (int idx = length - 1; idx >= 0; --idx) {
			assertEquals(data[idx], Array.get(translated, idx));
		}
	}

	@Test(expected = ClassCastException.class)
	public void testTranslate_NotSerializableComponentClass() throws Throwable {
		// Arrange
		final Stub[] data = { new Stub(), new Stub(), new Stub() };
		final Map<String, Serializable> map = new HashMap<>();
		final IContext context = new MapContext("foo", map);
		final ITranslator translator = new ArrayTranslator();

		// Act
		try {
			translator.translate(data, context);
		} catch (TranslationException e) {
			throw e.getCause();
		}
	}

	public static class Stub {}
}