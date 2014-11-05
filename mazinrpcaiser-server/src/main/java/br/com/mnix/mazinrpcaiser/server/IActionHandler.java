package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.InputAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/3/14.
 *
 * @author mnix05
 */
public interface IActionHandler {
	@Nullable Serializable processAction(@Nonnull InputAction action, @Nonnull IDataGrid dataGrid) throws Exception;
}
