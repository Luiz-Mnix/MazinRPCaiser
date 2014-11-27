package br.com.mnix.mazinrpcaiser.client.translation;

import br.com.mnix.mazinrpcaiser.client.proxy.IProxy;
import br.com.mnix.mazinrpcaiser.client.proxy.IProxyFactory;
import br.com.mnix.mazinrpcaiser.common.DistributedVersion;
import br.com.mnix.mazinrpcaiser.common.ProxiedObject;
import br.com.mnix.mazinrpcaiser.common.translation.TranslationException;
import javassist.NotFoundException;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Created by mnix05 on 11/21/14.
 *
 * @author mnix05
 */
@SuppressWarnings("serial")
public class ClientDataTranslatorTest {
	private static final String DEFAULT_ID = "foo";

	@Test
	public void testEncodeToServer_NullArgs() throws Throwable {
		// Arrange
		final IProxyFactory factory = new StubProxyFactory1();

		// Act
		final Object result = ClientDataTranslator.encodeToServer(null, factory);

		// Assert
		assertNull(result);
	}

	@Test
	public void testEncodeToServer_SomeArgs() throws Throwable {
		// Arrange
		final IProxyFactory factory = new StubProxyFactory1();
		final Object[] data = { DEFAULT_ID, new IStub() {} };

		// Act
		Object[] translated = (Object[]) ClientDataTranslator.encodeToServer(data, factory);

		// Assert
		assert translated != null;
		assertEquals(DEFAULT_ID, translated[0]);
		assertTrue(translated[1] instanceof ProxiedObject);
		assertEquals(DEFAULT_ID, ((ProxiedObject) translated[1]).getObjectId());
	}

	@Test(expected = TranslationException.class)
	public void testEncodeToServer_UnknownData() throws Throwable {
		// Arrange
		final IProxyFactory factory = new StubProxyFactory1();
		final Object[] data = { factory };

		// Act & Assert
		ClientDataTranslator.encodeToServer(data, factory);
	}

	@Test
	public void testDecodeFromServer_ProxiedObj() throws Throwable {
		// Arrange
		final IProxyFactory factory = new StubProxyFactory1();
		final ProxiedObject obj = new ProxiedObject(DEFAULT_ID, IDistributedStub.class);

		// Act
		Object translated = ClientDataTranslator.decodeFromServer(obj, factory);

		// Assert
		assertTrue(translated instanceof IDistributedStub);
	}

	@Test(expected = TranslationException.class)
	public void testDecodeFromServer_UnknownData() throws Throwable {
		// Arrange
		final IProxyFactory factory = new StubProxyFactory1();
		final Serializable obj = new IStub() {};

		// Act & Assert
		ClientDataTranslator.decodeFromServer(obj, factory);
	}

	private interface IStub extends Serializable {}
	@DistributedVersion(of = IStub.class) private interface IDistributedStub {}

	private static class StubProxyFactory1 implements IProxyFactory {
		public static final Class[] PARAM_TYPES = new Class[0];
		public static final Object[] ARGS = new Object[0];
		@Nonnull @Override public <T> T makeRemoteProxy(@Nonnull String id, boolean overwrites, @Nonnull Class<T> distributedInterface, Serializable... args) throws Exception {
			throw new UnsupportedOperationException();
		}
		@Nonnull @Override public <T> T makeProxy(@Nonnull final String id, @Nonnull final Class<T> distributedInterface) {
			IProxy mProxy = new IProxy() {
				@Nonnull @Override public Object proxyInterface() {
					throw new UnsupportedOperationException();
				}
				@Nonnull @Override public String getId() {
					return id;
				}
				@Nonnull @Override public Class<?> getBaseInterface() {
					return distributedInterface;
				}
				@Override public Object invoke(Object o, Method method, Method method2, Object[] objects) throws Throwable {
					throw new UnsupportedOperationException();
				}
			};

			javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();
			factory.setInterfaces(new Class[] {distributedInterface});
			try {
				// noinspection unchecked
				return (T) factory.create(PARAM_TYPES, ARGS, mProxy);
			} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new AssertionError(e);
			}
		}
		@Nonnull @Override public IProxy getProxyOfObject(@Nonnull Object obj) throws NotFoundException {
			return new IProxy() {
				@Nonnull @Override public Object proxyInterface() {
					throw new UnsupportedOperationException();
				}
				@Nonnull @Override public String getId() {
					return DEFAULT_ID;
				}
				@Nonnull @Override public Class<?> getBaseInterface() {
					return DEFAULT_ID.getClass();
				}
				@Override public Object invoke(Object o, Method method, Method method2, Object[] objects) throws Throwable {
					throw new UnsupportedOperationException();
				}
			};
		}
	}
}
