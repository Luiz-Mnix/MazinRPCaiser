package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DistributedVersion {
	@Nonnull Class<? extends Serializable> of();
}
