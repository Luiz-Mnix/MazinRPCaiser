package br.com.mnix.mazinrpcaiser.common.translation;

import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.assertSame;

public class UnnecessaryTranslatorTest {

	@Test
	public void testTranslate() throws Exception {
		// Arrange
		final Serializable data1 = 1;
		final Serializable data2 = 1.f;
		final Serializable data3 = "foo";
		final Serializable data4 = data3.getClass();
		final Serializable data5 = new IllegalArgumentException();
		final Serializable data6 = 'F';
		final UnnecessaryTranslator translator = new UnnecessaryTranslator();

		// Act & Assert
		assertSame(data1, translator.translate(data1, null));
		assertSame(data2, translator.translate(data2, null));
		assertSame(data3, translator.translate(data3, null));
		assertSame(data4, translator.translate(data4, null));
		assertSame(data5, translator.translate(data5, null));
		assertSame(data6, translator.translate(data6, null));
	}
}