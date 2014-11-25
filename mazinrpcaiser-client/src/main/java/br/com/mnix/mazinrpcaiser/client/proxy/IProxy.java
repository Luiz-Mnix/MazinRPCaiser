package br.com.mnix.mazinrpcaiser.client.proxy;

import javassist.util.proxy.MethodHandler;

import javax.annotation.Nonnull;

/**
 * Created by mnix05 on 11/21/14.
 *
 * @author mnix05
 */
public interface IProxy<T> extends MethodHandler {
	@Nonnull T proxyInterface();
	@Nonnull String getId();
	@Nonnull Class<T> getBaseInterface();
}
