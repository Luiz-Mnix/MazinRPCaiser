package br.com.mnix.mazinrpcaiser.client.translation;

import br.com.mnix.mazinrpcaiser.client.proxy.IProxy;
import br.com.mnix.mazinrpcaiser.client.proxy.IProxyFactory;
import br.com.mnix.mazinrpcaiser.common.DistributedVersion;
import br.com.mnix.mazinrpcaiser.common.ProxiedObject;
import br.com.mnix.mazinrpcaiser.common.translation.TranslationException;
import javassist.NotFoundException;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by mnix05 on 11/21/14.
 *
 * @author mnix05
 */
public class ServerObjectDecoderTest {

	public static final Class[] PARAM_TYPES = new Class[0];
	public static final Object[] ARGS = new Object[0];

	@Test(expected = IllegalArgumentException.class)
	public void testTranslate_NotProxiedObject() throws Throwable {
		// Arrange
		final IProxyFactory factory = new MockProxyFactory();
		final ServerObjectDecoder decoder = new ServerObjectDecoder(factory);

		// Act & Assert
		try {
			decoder.translate("fuck", null);
		} catch(TranslationException e) {
			throw e.getCause();
		}
	}

	@Test
	public void testTranslate_ProxiedObject() throws Throwable {
		// Arrange
		final IProxyFactory factory = new MockProxyFactory();
		final ServerObjectDecoder decoder = new ServerObjectDecoder(factory);
		final ProxiedObject data = new ProxiedObject("foo", IDistributedStub.class);

		// Act
		final IDistributedStub translated = (IDistributedStub) decoder.translate(data, null);
		final IProxy proxy = factory.getProxyOfObject(translated);

		// Assert
		Assert.assertEquals(data.getObjectId(), proxy.getId());
		Assert.assertEquals(data.getObjectClass(), proxy.getBaseInterface());
	}

	private interface IStub extends Serializable {}
	@DistributedVersion(of = IStub.class) private interface IDistributedStub {}

	private class MockProxyFactory implements IProxyFactory {
		private IProxy mProxy = null;
		@Nonnull @Override public <T> T makeRemoteProxy(@Nonnull String id, boolean overwrites, @Nonnull Class<T> distributedInterface, Serializable... args) throws Exception {
			throw new UnsupportedOperationException();
		}
		@Nonnull @Override public <T> T makeProxy(@Nonnull final String id, @Nonnull final Class<T> distributedInterface) {
			mProxy = new IProxy() {
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
			return mProxy;
		}
	}
}
