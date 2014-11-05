package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.CloseSessionData;
import br.com.mnix.mazinrpcaiser.common.IActionData;
import br.com.mnix.mazinrpcaiser.common.OpenSessionData;
import org.reflections.Reflections;

import java.util.Set;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public final class ServerApp {
	public static void main(String[] args) {
		Set<Class<? extends IActionData>> expectedDataClasses
				= new Reflections("br.com.mnix").getSubTypesOf(IActionData.class);

		IDataGrid dataGrid = DataGridFactory.getGrid();
		new Thread(new TaskReceiver<>(OpenSessionData.class, dataGrid)).start();
		new Thread(new TaskReceiver<>(CloseSessionData.class, dataGrid)).start();

		//TODO terminar de implementar
//		for(Class<? extends IActionData> metadataClass : expectedDataClasses) {
//			if(metadataClass.isAnnotationPresent(InputActionData.class)) {
//
//			}
//		}
	}
}
