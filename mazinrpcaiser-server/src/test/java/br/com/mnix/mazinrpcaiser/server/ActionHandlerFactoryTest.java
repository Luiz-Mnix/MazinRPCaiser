package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.IActionData;
import br.com.mnix.mazinrpcaiser.common.InputAction;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

import static org.junit.Assert.*;

public class ActionHandlerFactoryTest {

	@Test
	public void testHandlerForActionDataType_AnnotatedHandler() throws Exception {
		// Arrange & Act
		final IActionHandler handler = ActionHandlerFactory.handlerForActionDataType(AnnotatedData.class);

		// Assert
		assertEquals(Foo.class, handler.getClass());
	}

	@Test
	public void testHandlerForActionDataType_UnannotatedHandler() throws Exception {
		// Arrange & Act
		final IActionHandler handler = ActionHandlerFactory.handlerForActionDataType(UnannotatedData.class);

		// Assert
		assertEquals(UnannotatedDataHandler.class, handler.getClass());
	}

	@Test
	public void testHandlerForActionDataType_ProblematicHandlerAndSuperclass() throws Exception {
		// Arrange & Act
		final IActionHandler handler = ActionHandlerFactory.handlerForActionDataType(ProblematicData.class);

		// Assert
		assertEquals(AbstractProblematicDataHandler.class, handler.getClass());
	}

	@Test(expected = DataTypeHasNoHandlerException.class)
	public void testHandlerForActionDataType_HandlerlessData() throws Exception {
		// Arrange & Act
		ActionHandlerFactory.handlerForActionDataType(HandlerlessData.class);
	}

	// ANNOTATED HANDLER

	public static class AnnotatedData implements IActionData {
		private static final long serialVersionUID = -8894242395974266868L;
	}
	@ActionHandler(to = AnnotatedData.class)
	public static class Foo implements IActionHandler {
		@Nullable
		@Override
		public Serializable processAction(@Nonnull InputAction action, @Nonnull IDataGrid dataGrid) throws Exception {
			return null;
		}
	}

	// UNANNOTATED HANDLER

	public static class UnannotatedData implements IActionData {
		private static final long serialVersionUID = -8894242395974266868L;
	}
	public static class UnannotatedDataHandler implements IActionHandler {
		@Nullable
		@Override
		public Serializable processAction(@Nonnull InputAction action, @Nonnull IDataGrid dataGrid) throws Exception {
			return null;
		}
	}

	// PROBLEMATIC ANNOTATED

	public abstract static class AbstractProblematicData implements IActionData{
		private static final long serialVersionUID = -1899686166752208803L;
	}
	public static class ProblematicData extends AbstractProblematicData {
		private static final long serialVersionUID = -8894242395974266868L;
	}
	@ActionHandler(to = ProblematicData.class)
	public static class Bar implements IActionHandler {
		public Bar() throws Exception {
			throw new Exception();
		}
		@Nullable
		@Override
		public Serializable processAction(@Nonnull InputAction action, @Nonnull IDataGrid dataGrid) throws Exception {
			throw new Exception();
		}
	}
	public static class AbstractProblematicDataHandler implements IActionHandler {
		@Nullable
		@Override
		public Serializable processAction(@Nonnull InputAction action, @Nonnull IDataGrid dataGrid) throws Exception {
			return null;
		}
	}

	// HANDLERLESS DATA

	public static class HandlerlessData implements IActionData {
		private static final long serialVersionUID = -9188946213245111860L;
	}
}