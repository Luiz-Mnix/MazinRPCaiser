package br.com.mnix.mazinrpcaiser.client.translation;

import br.com.mnix.mazinrpcaiser.client.proxy.IProxy;
import br.com.mnix.mazinrpcaiser.client.proxy.IProxyFactory;
import br.com.mnix.mazinrpcaiser.common.DistributedVersion;
import br.com.mnix.mazinrpcaiser.common.ITransmissible;
import br.com.mnix.mazinrpcaiser.common.ProxiedObject;
import br.com.mnix.mazinrpcaiser.common.translation.TranslationException;
import javassist.NotFoundException;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * Created by mnix05 on 11/24/14.
 *
 * @author mnix05
 */
public class ClientObjectEncoderTest {
	private static final String FOUND_ID = "found";

	@Test
	public void testTranslate_foundObject() throws Exception {
		// Arrange
		final IProxyFactory factory = new MockProxyFactory();
		final ClientObjectEncoder encoder = new ClientObjectEncoder(factory);

		// Act
		final Object translated = encoder.translate(new IDistributedStub(){}, null);
		final ProxiedObject proxy = (ProxiedObject) translated;

		// Assert
		assertEquals(IDistributedStub.class, proxy.getObjectClass());
		assertEquals(FOUND_ID, proxy.getObjectId());
	}

	@Test(expected = NotFoundException.class)
	public void testTranslate_notFoundObject() throws Throwable {
		// Arrange
		final IProxyFactory factory = new MockProxyFactory();
		final ClientObjectEncoder encoder = new ClientObjectEncoder(factory);

		// Act & Assert
		try {
			encoder.translate("", null);
		} catch(TranslationException e) {
			throw e.getCause();
		}
	}

	private class MockProxy implements IProxy {
		@Nonnull @Override public Object proxyInterface() {
			throw new UnsupportedOperationException();
		}
		@Nonnull @Override public String getId() {
			return FOUND_ID;
		}
		@Nonnull @Override public Class<?> getBaseInterface() {
			return IDistributedStub.class;
		}
		@Override public Object invoke(Object o, Method method, Method method2, Object[] objects) throws Throwable {
			throw new UnsupportedOperationException();
		}
	}

	private class MockProxyFactory implements IProxyFactory {
		@Nonnull @Override public <T> T makeRemoteProxy(@Nonnull String id, boolean overwrites, @Nonnull Class<T> distributedInterface, Serializable... args) throws Exception {
			throw new UnsupportedOperationException();
		}
		@Nonnull @Override public <T> T makeProxy(@Nonnull String id, @Nonnull Class<T> distributedInterface) {
			throw new UnsupportedOperationException();
		}
		@Nonnull @Override public IProxy getProxyOfObject(@Nonnull Object obj) throws NotFoundException {
			if(obj instanceof IDistributedStub) {
				return new MockProxy();
			}

			throw new NotFoundException("UR MOMMA SO FAT");
		}
	}

	private interface IStub extends Serializable {}
	@DistributedVersion(of = IStub.class)private interface IDistributedStub {}
}
