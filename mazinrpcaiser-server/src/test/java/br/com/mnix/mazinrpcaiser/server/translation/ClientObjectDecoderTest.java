package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.common.ProxiedObject;
import br.com.mnix.mazinrpcaiser.common.exception.ObjectNotFoundException;
import br.com.mnix.mazinrpcaiser.common.translation.TranslationException;
import br.com.mnix.mazinrpcaiser.server.data.MapContext;
import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class ClientObjectDecoderTest {
	@Test
	public void testTranslate_FoundObject() throws Exception {
		// Arrange
		final String id = "foo";
		final HashMap<String, Serializable> innerMap = new HashMap<>();
		final MapContext context = new MapContext(id, innerMap);
		final ClientObjectDecoder decoder = new ClientObjectDecoder(context);
		final ProxiedObject obj = new ProxiedObject(id, String.class);

		// Act
		innerMap.put(id, id);

		// Assert
		assertEquals(id, decoder.translate(obj, null));
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testTranslate_NotFoundObject() throws Throwable {
		// Arrange
		final String id = "foo";
		final HashMap<String, Serializable> innerMap = new HashMap<>();
		final MapContext context = new MapContext(id, innerMap);
		final ClientObjectDecoder decoder = new ClientObjectDecoder(context);
		final ProxiedObject obj = new ProxiedObject(id, String.class);

		// Act & Assert
		try {
			decoder.translate(obj, null);
		} catch(TranslationException ex) {
			throw ex.getCause();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTranslate_NotProxiedObject() throws Throwable {
		// Arrange
		final String id = "foo";
		final HashMap<String, Serializable> innerMap = new HashMap<>();
		final MapContext context = new MapContext(id, innerMap);
		final ClientObjectDecoder decoder = new ClientObjectDecoder(context);

		// Act & Assert
		try {
			decoder.translate(id, null);
		} catch(TranslationException ex) {
			throw ex.getCause();
		}
	}
}