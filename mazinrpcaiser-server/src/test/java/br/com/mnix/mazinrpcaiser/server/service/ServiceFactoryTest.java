package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

import static org.junit.Assert.*;

public class ServiceFactoryTest {

	@Test
	public void testHandlerForActionDataType_AnnotatedHandler() throws Exception {
		// Arrange & Act
		final IService handler = ServiceFactory.getServiceForRequest(AnnotatedData.class);

		// Assert
		assertEquals(Foo.class, handler.getClass());
	}

	@Test
	public void testHandlerForActionDataType_UnannotatedHandler() throws Exception {
		// Arrange & Act
		final IService handler = ServiceFactory.getServiceForRequest(UnannotatedRequest.class);

		// Assert
		assertEquals(UnannotatedService.class, handler.getClass());
	}

	@Test
	public void testHandlerForActionDataType_ProblematicHandlerAndSuperclass() throws Exception {
		// Arrange & Act
		final IService handler = ServiceFactory.getServiceForRequest(ProblematicRequest.class);

		// Assert
		assertEquals(AbstractProblematicService.class, handler.getClass());
	}

	@Test(expected = RequestHasNoServiceException.class)
	public void testHandlerForActionDataType_HandlerlessData() throws Exception {
		// Arrange & Act
		ServiceFactory.getServiceForRequest(HandlerlessRequest.class);
	}

	// ANNOTATED HANDLER

	public static class AnnotatedData implements Serializable {
		private static final long serialVersionUID = -8894242395974266868L;
	}
	@Service(forRequest = AnnotatedData.class)
	public static class Foo implements IService {
		@Nullable
		@Override
		public Serializable processAction(@Nonnull RequestEnvelope action, @Nonnull IDataGrid dataGrid) throws Exception {
			return null;
		}
	}

	// UNANNOTATED HANDLER

	public static class UnannotatedRequest implements Serializable {
		private static final long serialVersionUID = -8894242395974266868L;
	}
	public static class UnannotatedService implements IService {
		@Nullable
		@Override
		public Serializable processAction(@Nonnull RequestEnvelope action, @Nonnull IDataGrid dataGrid) throws Exception {
			return null;
		}
	}

	// PROBLEMATIC ANNOTATED

	public abstract static class AbstractProblematicRequest implements Serializable{
		private static final long serialVersionUID = -1899686166752208803L;
	}
	public static class ProblematicRequest extends AbstractProblematicRequest {
		private static final long serialVersionUID = -8894242395974266868L;
	}
	@Service(forRequest = ProblematicRequest.class)
	public static class Bar implements IService {
		public Bar() throws Exception {
			throw new Exception();
		}
		@Nullable
		@Override
		public Serializable processAction(@Nonnull RequestEnvelope action, @Nonnull IDataGrid dataGrid) throws Exception {
			throw new Exception();
		}
	}
	public static class AbstractProblematicService implements IService {
		@Nullable
		@Override
		public Serializable processAction(@Nonnull RequestEnvelope action, @Nonnull IDataGrid dataGrid) throws Exception {
			return null;
		}
	}

	// HANDLERLESS DATA

	public static class HandlerlessRequest implements Serializable {
		private static final long serialVersionUID = -9188946213245111860L;
	}
}