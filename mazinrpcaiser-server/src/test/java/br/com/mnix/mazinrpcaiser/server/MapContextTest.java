package br.com.mnix.mazinrpcaiser.server;

import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;

import static org.junit.Assert.*;

@SuppressWarnings("MagicNumber")
public class MapContextTest {
	@Test
	public void testAddObject() throws Exception {
		// Arrange
		final HashMap<String, Serializable> innerMap = new HashMap<>();
		final MapContext context = new MapContext(innerMap);

		// Act
		innerMap.put("key1", "foo");
		innerMap.put("key2", 1);
		context.putObject("key2", "bar");
		context.putObject("key3", 666);

		// Assert
		assertTrue(context.containsObject("key1"));
		assertTrue(innerMap.containsKey("key3"));
		assertEquals(innerMap.get("key2"), context.getSerializable("key2"));
		assertEquals(innerMap.size(), context.size());
	}
}