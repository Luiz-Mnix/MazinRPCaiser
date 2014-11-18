package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.common.NotTransmissibleObject;
import br.com.mnix.mazinrpcaiser.server.data.DataGridFactory;
import br.com.mnix.mazinrpcaiser.server.data.IContext;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import br.com.mnix.mazinrpcaiser.server.data.MapContext;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ProxiedObjectTranslatorTest {
	@Test
	public void testTranslate_DistributedContext() throws Exception {
		// Arrange
		final String id = "foo";
		final IDataGrid grid = DataGridFactory.getGrid();
		final StubEquals stub1 = new StubEquals(id);
		final ProxiedObjectTranslator translator = new ProxiedObjectTranslator();

		// Act
		grid.raise();
		final IContext context = grid.retrieveContext(id, false);
		final NotTransmissibleObject proxy = (NotTransmissibleObject) translator.translate(stub1, context);
		final NotTransmissibleObject proxy2 = (NotTransmissibleObject) translator.translate(stub1, context);

		// Assert
		assertEquals(1, context.size());
		assertTrue(context.containsObjectId(proxy.getObjectId()));
		assertEquals(stub1, context.getSerializable(proxy.getObjectId()));
		assertEquals(stub1.getClass(), proxy.getObjectClass());
		assertEquals(proxy.getObjectId(), proxy2.getObjectId());

		grid.shutdown();
	}

	@Test
	public void testTranslate_SimpleContext() throws Exception {
		// Arrange
		final Map<String, Serializable> contextMap = new HashMap<>();
		final String id = "foo";
		final IContext context = new MapContext(id, contextMap);
		final StubEquals stub1 = new StubEquals(id);
		final ProxiedObjectTranslator translator = new ProxiedObjectTranslator();

		// Act
		final NotTransmissibleObject proxy = (NotTransmissibleObject) translator.translate(stub1, context);
		final NotTransmissibleObject proxy2 = (NotTransmissibleObject) translator.translate(stub1, context);

		// Assert
		assertEquals(1, context.size());
		assertTrue(context.containsObjectId(proxy.getObjectId()));
		assertSame(stub1, context.getSerializable(proxy.getObjectId()));
		assertEquals(stub1.getClass(), proxy.getObjectClass());
		assertEquals(proxy.getObjectId(), proxy2.getObjectId());
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