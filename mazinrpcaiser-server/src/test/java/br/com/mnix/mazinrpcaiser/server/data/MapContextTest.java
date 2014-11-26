package br.com.mnix.mazinrpcaiser.server.data;

import br.com.mnix.mazinrpcaiser.common.exception.ObjectNotFoundException;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@SuppressWarnings("MagicNumber")
public class MapContextTest {
	@Test
	public void testAddObject() throws Exception {
		// Arrange
		final HashMap<String, Serializable> innerMap = new HashMap<>();
		final MapContext context = new MapContext("foo", innerMap);

		// Act
		innerMap.put("key1", "foo");
		innerMap.put("key2", 1);
		context.putObject("key2", "bar");
		context.putObject("key3", 666);

		// Assert
		assertTrue(context.containsObjectId("key1"));
		assertTrue(innerMap.containsKey("key3"));
		assertEquals(innerMap.get("key2"), context.getSerializable("key2"));
		assertEquals(innerMap.size(), context.size());
	}

	@Test
	public void test_SimpleContext_NotOverriddenEquals() throws Exception {
		// Arrange
		final Map<String, Serializable> contextMap = new HashMap<>();
		final String id = "foo";
		final IContext context = new MapContext(id, contextMap);
		final StubNotEquals stub1 = new StubNotEquals(id);
		final StubNotEquals stub2 = new StubNotEquals(stub1.getFoo());

		// Act
		context.putObject(id, stub1);

		// Assert
		assertTrue(context.containsObjectId(id));
		assertEquals(id, context.getObjectId(stub1));
		try {
			context.getObjectId(stub2);
			throw new Exception("Object which was not supposed to be found was found");
		} catch (ObjectNotFoundException ignored) {}
		assertFalse(context.containsObject(stub2));
		assertSame(stub1, context.getSerializable(id));
	}

	@Test(expected = ObjectNotFoundException.class)
	public void test_SimpleContext_GetObjectById() throws Exception {
		// Arrange
		final Map<String, Serializable> contextMap = new HashMap<>();
		final String id = "foo";
		final IContext context = new MapContext(id, contextMap);

		// Act & Assert
		context.getSerializable(id);
	}

	@Test
	public void test_DistributedContext_NotOverriddenEquals() throws Exception {
		// Arrange
		final String id = "foo";
		final IDataGrid grid = DataGridFactory.getGrid();
		final StubNotEquals stub1 = new StubNotEquals(id);
		final StubNotEquals stub2 = new StubNotEquals(stub1.getFoo());

		// Act
		grid.raise();
		final IContext context = grid.retrieveContext(id, false);
		context.putObject(id, stub1);

		// Assert
		assertTrue(context.containsObjectId(id));
		try {
			context.getObjectId(stub1);
			throw new Exception("Object which was not supposed to be found was found");
		} catch(ObjectNotFoundException ignored) {}
		try {
			context.getObjectId(stub2);
			throw new Exception("Object which was not supposed to be found was found");
		} catch(ObjectNotFoundException ignored) {}
		assertTrue(context.containsObject(stub2));
		assertNotEquals(stub1, context.getSerializable(id));

		grid.shutdown();
	}

	@Test
	public void test_SimpleContext_OverriddenEquals() throws Exception {
		// Arrange
		final Map<String, Serializable> contextMap = new HashMap<>();
		final String id = "foo";
		final IContext context = new MapContext(id, contextMap);
		final StubEquals stub1 = new StubEquals(id);
		final StubEquals stub2 = new StubEquals(stub1.getFoo());

		// Act
		context.putObject(id, stub1);

		// Assert
		assertTrue(context.containsObjectId(id));
		assertEquals(id, context.getObjectId(stub1));
		assertEquals(id, context.getObjectId(stub2));
		assertTrue(context.containsObject(stub2));
		assertSame(stub1, context.getSerializable(id));
	}

	@Test
	public void test_DistributedContext_OverriddenEquals() throws Exception {
		// Arrange
		final String id = "foo";
		final IDataGrid grid = DataGridFactory.getGrid();
		final StubEquals stub1 = new StubEquals(id);
		final StubEquals stub2 = new StubEquals(stub1.getFoo());

		// Act
		grid.raise();
		final IContext context = grid.retrieveContext(id, false);
		context.putObject(id, stub1);

		// Assert
		assertTrue(context.containsObjectId(id));
		assertEquals(id, context.getObjectId(stub1));
		assertEquals(id, context.getObjectId(stub2));
		assertTrue(context.containsObject(stub2));
		assertEquals(stub1, context.getSerializable(id));

		grid.shutdown();
	}

	public static class StubNotEquals implements Serializable {
		private static final long serialVersionUID = 4104309686454365439L;
		@Nonnull
		private final String mFoo;
		public StubNotEquals(@Nonnull String foo) {
			mFoo = foo;
		}
		@Nonnull public String getFoo() {
			return mFoo;
		}
	}

	public static class StubEquals implements Serializable {
		private static final long serialVersionUID = 4104309686454365439L;
		@Nonnull private final String mFoo;
		public StubEquals(@Nonnull String foo) {
			mFoo = foo;
		}
		@Nonnull public String getFoo() {
			return mFoo;
		}
		@Override
		public boolean equals(Object obj) {
			return obj != null && obj instanceof StubEquals && ((StubEquals)obj).getFoo().equals(mFoo);
		}
		@Override
		public int hashCode() {
			return mFoo.hashCode() * 3;
		}
	}
}