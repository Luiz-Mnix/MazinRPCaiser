package br.com.mnix.mazinrpcaiser.common.translation;

import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@SuppressWarnings({"unchecked", "ThrowableResultOfMethodCallIgnored"})
public class MapTranslatorTest {
	@Test(expected = ClassCastException.class)
	public void testTranslate_notMap() throws Exception{
		// Arrange
		final Serializable data = "a";
		final MapTranslator translator = new MapTranslator();

		// Act & Arrange
		try {
			translator.translate(data, null);
		} catch(TranslationException ex) {
			throw (Exception) ex.getCause();
		}
	}

	@Test
	public void test_HashMap() throws Exception {
		// Arrange
		final HashMap<Exception, Exception> data = new HashMap<>();
		final MapTranslator translator = new MapTranslator();

		// Act
		data.put(new Exception("foo"), new Exception("foo1"));
		data.put(new Exception("bar"), new Exception("bar1"));
		data.put(new Exception("baz"), new Exception("baz1"));
		data.put(new Exception("zap"), new Exception("zap1"));
		HashMap<Exception, Exception> translatedData = (HashMap<Exception, Exception>) translator.translate(data, null);

		// Assert
		assertEquals(data.size(), translatedData.size());
		for (Map.Entry<Exception, Exception> entry : data.entrySet()) {
			assertTrue(translatedData.containsKey(entry.getKey()));
			assertSame(entry.getValue(), translatedData.get(entry.getKey()));
		}
	}
}