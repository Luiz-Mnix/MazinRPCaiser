package br.com.mnix.mazinrpcaiser.server.service;

import br.com.mnix.mazinrpcaiser.common.RequestEnvelope;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public interface IService {
	@SuppressWarnings("RedundantThrows")
	@Nullable Serializable processRequest(@Nonnull RequestEnvelope requestEnv, @Nonnull IDataGrid dataGrid)
			throws Throwable;
}
