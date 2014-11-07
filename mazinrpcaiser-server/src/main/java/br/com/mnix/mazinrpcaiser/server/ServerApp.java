package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.request.CloseSessionRequest;
import br.com.mnix.mazinrpcaiser.common.request.OpenSessionRequest;
import br.com.mnix.mazinrpcaiser.server.data.DataGridFactory;
import br.com.mnix.mazinrpcaiser.server.data.IDataGrid;
import org.reflections.Reflections;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public final class ServerApp {
	public static void main(String[] args) throws Exception {
		Set<Class<? extends Serializable>> expectedDataClasses
				= new Reflections("br.com.mnix").getSubTypesOf(Serializable.class);

		IDataGrid dataGrid = DataGridFactory.getGrid();
		new Thread(new TaskReceiver<>(OpenSessionRequest.class, dataGrid)).start();
		new Thread(new TaskReceiver<>(CloseSessionRequest.class, dataGrid)).start();

		//TODO terminar de implementar
//		for(Class<? extends IActionData> metadataClass : expectedDataClasses) {
//			if(metadataClass.isAnnotationPresent(InputActionData.class)) {
//
//			}
//		}
	}
}
