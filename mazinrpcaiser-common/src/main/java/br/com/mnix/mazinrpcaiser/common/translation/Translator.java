package br.com.mnix.mazinrpcaiser.common.translation;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by mnix05 on 11/11/14.
 *
 * @author mnix05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Translator {
	@Nonnull Class<?>[] of();
}
