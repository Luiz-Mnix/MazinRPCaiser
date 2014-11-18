package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.server.data.IContext;
import br.com.mnix.mazinrpcaiser.server.data.MapContext;
import org.junit.Test;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class CollectionTranslatorTest {
	@Test(expected = ClassCastException.class)
	public void testTranslate_NotCollection() throws Exception {
		// Arrange
		final Serializable data = "a";
		final CollectionTranslator translator = new CollectionTranslator();
		final IContext context = new MapContext("foo", new HashMap<String, Serializable>());

		// Act & Arrange
		try {
			translator.translate(data, context);
		} catch(TranslationException ex) {
			throw (Exception) ex.getCause();
		}
	}

	@Test
	public void test_HashSet() throws Exception {
		// Arrange
		final HashSet<Exception> data = new HashSet<>();
		final CollectionTranslator translator = new CollectionTranslator();
		final IContext context = new MapContext("foo", new HashMap<String, Serializable>());

		// Act
		data.add(new Exception("foo"));
		data.add(new Exception("bar"));
		data.add(new Exception("baz"));
		data.add(new Exception("zap"));
		Set<Exception> translatedData = (Set<Exception>) translator.translate(data, context);

		// Assert
		assertEquals(data.size(), translatedData.size());
		for (Exception exception : translatedData) {
			assertTrue(data.contains(exception));
		}
	}

	@Test
	public void test_ArrayList() throws Exception {
		// Arrange
		final ArrayList<Exception> data = new ArrayList<>();
		final CollectionTranslator translator = new CollectionTranslator();
		final IContext context = new MapContext("foo", new HashMap<String, Serializable>());

		// Act
		data.add(new Exception("foo"));
		data.add(new Exception("bar"));
		data.add(new Exception("baz"));
		data.add(new Exception("zap"));
		List<Exception> translatedData = (List<Exception>) translator.translate(data, context);

		// Assert
		assertEquals(data.size(), translatedData.size());
		for (int idx = data.size() - 1; idx >= 0; idx--) {
			assertSame(data.get(idx), translatedData.get(idx));
		}
	}

	@Test
	public void test_ArrayBlockingQueue() throws Exception {
		// Arrange
		final ArrayBlockingQueue<Exception> data = new ArrayBlockingQueue<>(4);
		final CollectionTranslator translator = new CollectionTranslator();
		final IContext context = new MapContext("foo", new HashMap<String, Serializable>());

		// Act
		data.add(new Exception("foo"));
		data.add(new Exception("bar"));
		data.add(new Exception("baz"));
		data.add(new Exception("zap"));
		ArrayBlockingQueue<Exception> translatedData = (ArrayBlockingQueue<Exception>) translator.translate(data, context);

		// Assert
		assertEquals(data.size(), translatedData.size());
		for (Exception exception : data) {
			assertSame(exception, translatedData.poll());
		}
	}

	@Test
	public void test_Stack() throws Exception {
		// Arrange
		final Stack<Exception> data = new Stack<>();
		final CollectionTranslator translator = new CollectionTranslator();
		final IContext context = new MapContext("foo", new HashMap<String, Serializable>());

		// Act
		data.add(new Exception("foo"));
		data.add(new Exception("bar"));
		data.add(new Exception("baz"));
		data.add(new Exception("zap"));
		Stack<Exception> translatedData = (Stack<Exception>) translator.translate(data, context);

		// Assert
		assertEquals(data.size(), translatedData.size());
		while(!data.isEmpty()) {
			assertSame(data.pop(), translatedData.pop());
		}
	}
}