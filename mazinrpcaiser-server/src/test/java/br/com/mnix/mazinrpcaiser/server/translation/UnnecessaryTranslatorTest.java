package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.server.data.IContext;
import br.com.mnix.mazinrpcaiser.server.data.MapContext;
import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;

import static org.junit.Assert.*;

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
		final IContext context = new MapContext("foo", new HashMap<String, Serializable>());

		// Act & Assert
		assertSame(data1, translator.translate(data1, context));
		assertSame(data2, translator.translate(data2, context));
		assertSame(data3, translator.translate(data3, context));
		assertSame(data4, translator.translate(data4, context));
		assertSame(data5, translator.translate(data5, context));
		assertSame(data6, translator.translate(data6, context));
	}
}