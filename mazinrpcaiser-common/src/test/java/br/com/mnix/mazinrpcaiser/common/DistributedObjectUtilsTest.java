package br.com.mnix.mazinrpcaiser.common;

import br.com.mnix.mazinrpcaiser.common.exception.IllegalDistributedTypeException;
import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.lang.reflect.Method;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
public class DistributedObjectUtilsTest {
	@Test
	public void testAssert_CorrectDistributedInterface() throws Exception {
		// Arrange
		final Class<?> interfaceClass = IDistributedStub.class;

		// Act & assert
		DistributedObjectUtils.assertDistributedType(interfaceClass);
	}

	@Test(expected = IllegalDistributedTypeException.class)
	public void testAssert_NonInterfaceDistributedInterface() throws Exception {
		// Arrange
		final Class<?> interfaceClass = NonDefaultStubStub.class;

		// Act & assert
		DistributedObjectUtils.assertDistributedType(interfaceClass);
	}

	@Test(expected = IllegalDistributedTypeException.class)
	public void testAssert_NonDistributedInterface() throws Exception {
		// Arrange
		final Class<?> interfaceClass = IFake.class;

		// Act & assert
		DistributedObjectUtils.assertDistributedType(interfaceClass);
	}

	@Test
	public void testAssert_CorrectImplementation() throws Exception {
		// Arrange
		final Class<?> interfaceClass = IDistributedStub.class;
		final Class<?> implementationClass = NonDefaultStubStub.class;

		// Act & assert
		DistributedObjectUtils.assertDistributedType(interfaceClass, implementationClass);
	}

	@Test(expected = IllegalDistributedTypeException.class)
	public void testAssert_InterfaceImplementation() throws Exception {
		// Arrange
		final Class<?> interfaceClass = IDistributedStub.class;
		final Class<?> implementationClass = IDistributedStub.class;

		// Act & assert
		DistributedObjectUtils.assertDistributedType(interfaceClass, implementationClass);
	}

	@Test(expected = IllegalDistributedTypeException.class)
	public void testAssert_DifferentImplementation() throws Exception {
		// Arrange
		final Class<?> interfaceClass = IDistributedStub.class;
		final Class<?> implementationClass = FakeStubStub.class;

		// Act & assert
		DistributedObjectUtils.assertDistributedType(interfaceClass, implementationClass);
	}

	@Test(expected = IllegalDistributedTypeException.class)
	public void testAssert_DifferentMethodsBackendInterface() throws Exception {
		// Arrange
		final Class<?> interfaceClass = IDistributedMethodStub.class;

		// Act & assert
		DistributedObjectUtils.assertDistributedType(interfaceClass);
	}

	@Test
	public void testIsTypeMapped_SameJava() throws Exception {
		// Arrange
		final Class<?> type = Integer.class;

		// Act & assert
		Assert.assertTrue(DistributedObjectUtils.isDistributedTypeMapped(type, type));
	}

	@Test
	public void testIsTypeMapped_DifferentJava() throws Exception {
		// Arrange
		final Class<?> type = Integer.class;
		final Class<?> type2 = Float.class;

		// Act & assert
		Assert.assertFalse(DistributedObjectUtils.isDistributedTypeMapped(type, type2));
	}

	@Test
	public void testIsTypeMapped_SameTransmissible() throws Exception {
		// Arrange
		final Class<?> type = IITransmissibleStub.class;

		// Act & assert
		Assert.assertTrue(DistributedObjectUtils.isDistributedTypeMapped(type, type));
	}

	@Test
	public void testIsTypeMapped_DifferentTransmissible() throws Exception {
		// Arrange
		final Class<?> type = IITransmissibleStub.class;
		final Class<?> type2 = IITransmissibleStub2.class;

		// Act & assert
		Assert.assertFalse(DistributedObjectUtils.isDistributedTypeMapped(type, type2));
	}

	@Test
	public void testIsTypeMapped_EffedUpDistributed() throws Exception {
		// Arrange
		final Class<?> type = IMethodStub.class;
		final Class<?> type2 = IDistributedMethodStub.class;

		// Act & assert
		Assert.assertFalse(DistributedObjectUtils.isDistributedTypeMapped(type, type2));
	}

	@Test
	public void testIsTypeMapped_CorrectDistributed() throws Exception {
		// Arrange
		final Class<?> type = IDefaultStub.class;
		final Class<?> type2 = IDistributedStub.class;

		// Act & assert
		Assert.assertTrue(DistributedObjectUtils.isDistributedTypeMapped(type, type2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsMethodMapped_NotDistributedVersion() throws Exception {
		// Arrange
		final Method method = IDefaultStub.class.getDeclaredMethods()[0];

		// Act & assert
		DistributedObjectUtils.isDistributedMethodMapped(method);
	}

	@Test
	public void testIsMethodMapped_DifferentParams() throws Exception {
		// Arrange
		final Method method = IDistributedMethodStub.class.getDeclaredMethod("meh", Integer.class);

		// Act & assert
		Assert.assertFalse(DistributedObjectUtils.isDistributedMethodMapped(method));
	}

	@Test
	public void testIsMethodMapped_DifferentReturnType() throws Exception {
		// Arrange
		final Method method = IDistributedMethodStub.class.getDeclaredMethod("meh2");

		// Act & assert
		Assert.assertFalse(DistributedObjectUtils.isDistributedMethodMapped(method));
	}

	@Test
	public void testIsMethodMapped_DifferentReturnName() throws Exception {
		// Arrange
		final Method method = IDistributedMethodStub.class.getDeclaredMethod("meh3");

		// Act & assert
		Assert.assertFalse(DistributedObjectUtils.isDistributedMethodMapped(method));
	}

	@Test
	public void testIsMethodMapped_MappedType() throws Exception {
		// Arrange
		final Method method = IDistributedMethodStub.class.getDeclaredMethod("meh4");

		// Act & assert
		Assert.assertTrue(DistributedObjectUtils.isDistributedMethodMapped(method));
	}
	
	// DEFAULT IMPLEMENTATION STUB
	private interface IDefaultStub extends Serializable { void foo(); }
	private class NonDefaultStubStub implements IDefaultStub {
		private static final long serialVersionUID = -6336135849860695589L;
		@Override
		public void foo() {}
	}
	private class FakeStubStub {}
	@DistributedVersion(of = IDefaultStub.class) private interface IDistributedStub extends Serializable { void foo(); }

	// Baseless Service
	private interface IFake {}

	// Different methods
	private interface IMethodStub extends Serializable {
		void meh();
		void meh2();
		IDefaultStub meh4();
	}
	@DistributedVersion(of = IMethodStub.class) private interface IDistributedMethodStub {
		void meh(Integer foo);
		String meh2();
		void meh3();
		IDistributedStub meh4();
	}

	// Transmission
	private interface IITransmissibleStub extends ITransmissible {}
	private interface IITransmissibleStub2 extends ITransmissible {}
}