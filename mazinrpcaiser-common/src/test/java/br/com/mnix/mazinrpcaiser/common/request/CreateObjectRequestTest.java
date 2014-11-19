package br.com.mnix.mazinrpcaiser.common.request;

import br.com.mnix.mazinrpcaiser.common.DistributedVersion;
import org.junit.Test;

import java.io.Serializable;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
public class CreateObjectRequestTest {
	@Test
	public void testValidate_CorrectDistributedInterface() throws Exception {
		// Arrange
		final Class<?> interfaceClass = IDistributedStub.class;

		// Act & assert
		CreateObjectRequest.validate(interfaceClass, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidate_NonInterfaceDistributedInterface() throws Exception {
		// Arrange
		final Class<?> interfaceClass = NonDefaultStubStub.class;

		// Act & assert
		CreateObjectRequest.validate(interfaceClass, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidate_NonDistributedInterface() throws Exception {
		// Arrange
		final Class<?> interfaceClass = IFake.class;

		// Act & assert
		CreateObjectRequest.validate(interfaceClass, null);
	}

	@Test
	public void testValidate_CorrectImplementation() throws Exception {
		// Arrange
		final Class<?> interfaceClass = IDistributedStub.class;
		final Class<?> implementationClass = NonDefaultStubStub.class;

		// Act & assert
		CreateObjectRequest.validate(interfaceClass, implementationClass);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidate_InterfaceImplementation() throws Exception {
		// Arrange
		final Class<?> interfaceClass = IDistributedStub.class;
		final Class<?> implementationClass = IDistributedStub.class;

		// Act & assert
		CreateObjectRequest.validate(interfaceClass, implementationClass);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidate_DifferentImplementation() throws Exception {
		// Arrange
		final Class<?> interfaceClass = IDistributedStub.class;
		final Class<?> implementationClass = FakeStubStub.class;

		// Act & assert
		CreateObjectRequest.validate(interfaceClass, implementationClass);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidate_NonSerializableBackendInterface() throws Exception {
		// Arrange
		final Class<?> interfaceClass = IDistributedFake.class;

		// Act & assert
		CreateObjectRequest.validate(interfaceClass, null);
	}

	// DEFAULT IMPLEMENTATION STUB
	private interface IDefaultStub extends Serializable {}
	private class NonDefaultStubStub implements IDefaultStub {
		private static final long serialVersionUID = -6336135849860695589L;
	}
	private class FakeStubStub {}
	@DistributedVersion(of = IDefaultStub.class) private interface IDistributedStub extends Serializable {}

	// Baseless Service
	private interface IFake {}
	@DistributedVersion(of = IFake.class) private interface IDistributedFake {}
}