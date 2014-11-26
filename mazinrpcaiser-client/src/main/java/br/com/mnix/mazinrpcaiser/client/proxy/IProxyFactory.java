package br.com.mnix.mazinrpcaiser.client.proxy;

import javassist.NotFoundException;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/20/14.
 *
 * @author mnix05
 */
public interface IProxyFactory {
	@Nonnull <T> T makeRemoteProxy(@Nonnull String id, boolean overwrites, @Nonnull Class<T> distributedInterface,
								   Serializable... args) throws Exception;
	@Nonnull <T> T makeProxy(@Nonnull String id, @Nonnull Class<T> distributedInterface);
	@Nonnull IProxy getProxyOfObject(@Nonnull Object obj) throws NotFoundException;
}
