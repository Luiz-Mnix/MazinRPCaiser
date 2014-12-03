package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.common.DistributedVersion;
import br.com.mnix.mazinrpcaiser.common.ProxiedObject;
import br.com.mnix.mazinrpcaiser.common.translation.TranslationException;
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

public class ServerObjectEncoderTest {
	@Test
	public void testTranslate_DistributedContext() throws Exception {
		// Arrange
		final String id = "foo";
		final IDataGrid grid = DataGridFactory.makeDefaultDataGrid();
		final StubEquals stub1 = new StubEquals(id);

		// Act
		grid.raise();
		final IContext context = grid.retrieveContext(id, false);
		final ServerObjectEncoder translator = new ServerObjectEncoder(context);
		final ProxiedObject proxy = (ProxiedObject) translator.translate(stub1, null);
		final ProxiedObject proxy2 = (ProxiedObject) translator.translate(stub1, null);

		// Assert
		assertEquals(1, context.size());
		assertTrue(context.containsObjectId(proxy.getObjectId()));
		assertEquals(stub1, context.getSerializable(proxy.getObjectId()));
		assertEquals(IDistributedStubEquals.class, proxy.getObjectClass());
		assertEquals(proxy.getObjectId(), proxy2.getObjectId());

		grid.shutdown();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTranslate_UnSerializableData() throws Throwable {
		// Arrange
		final String id = "foo";
		final HashMap<String, Serializable> innerMap = new HashMap<>();
		final IContext context = new MapContext(id, innerMap);
		final Object stub1 = new Object();
		final ServerObjectEncoder translator = new ServerObjectEncoder(context);

		// Act & Assert
		try {
			translator.translate(stub1, null);
		} catch(TranslationException ex) {
			throw ex.getCause();
		}
	}

	@Test
	public void testTranslate_SimpleContext() throws Exception {
		// Arrange
		final Map<String, Serializable> contextMap = new HashMap<>();
		final String id = "foo";
		final IContext context = new MapContext(id, contextMap);
		final StubEquals stub1 = new StubEquals(id);
		final ServerObjectEncoder translator = new ServerObjectEncoder(context);

		// Act
		final ProxiedObject proxy = (ProxiedObject) translator.translate(stub1, null);
		final ProxiedObject proxy2 = (ProxiedObject) translator.translate(stub1, null);

		// Assert
		assertEquals(1, context.size());
		assertTrue(context.containsObjectId(proxy.getObjectId()));
		assertSame(stub1, context.getSerializable(proxy.getObjectId()));
		assertEquals(IDistributedStubEquals.class, proxy.getObjectClass());
		assertEquals(proxy.getObjectId(), proxy2.getObjectId());
	}

	private interface IStubEquals extends Serializable {}
	@DistributedVersion(of = IStubEquals.class) private interface IDistributedStubEquals {}

	private static class StubEquals implements IStubEquals {
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