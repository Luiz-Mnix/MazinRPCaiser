package br.com.mnix.mazinrpcaiser.server;

import br.com.mnix.mazinrpcaiser.common.IActionData;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by mnix05 on 11/7/14.
 *
 * @author mnix05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ActionHandler {
	@Nonnull Class<? extends IActionData> to();
}
