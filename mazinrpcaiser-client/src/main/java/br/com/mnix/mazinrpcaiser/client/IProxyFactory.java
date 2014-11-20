package br.com.mnix.mazinrpcaiser.client;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/20/14.
 *
 * @author mnix05
 */
public interface IProxyFactory {
	@Nonnull <T> T getProxy(@Nonnull String id, boolean overwrites, @Nonnull Class<T> distributedInterface,
							Serializable... args) throws Exception;
}
