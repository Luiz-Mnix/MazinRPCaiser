package br.com.mnix.mazinrpcaiser.common.translation;

import org.junit.Test;

import java.lang.reflect.Array;

import static org.junit.Assert.*;

public class ArrayTranslatorTest {

	@Test
	public void testTranslate_SerializableComponentClass() throws Exception {
		// Arrange
		final String[] data = { "foo", "bar", "bor" };
		final ITranslator translator = new ArrayTranslator();

		// Act
		Object[] translated = (Object[]) translator.translate(data, null);

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
		final ITranslator translator = new ArrayTranslator();

		// Act
		Object translated = translator.translate(data, null);

		// Assert
		int length = Array.getLength(translated);
		assertEquals(data.length, length);
		for (int idx = length - 1; idx >= 0; --idx) {
			assertEquals(data[idx], Array.get(translated, idx));
		}
	}

	public static class Stub {}
}