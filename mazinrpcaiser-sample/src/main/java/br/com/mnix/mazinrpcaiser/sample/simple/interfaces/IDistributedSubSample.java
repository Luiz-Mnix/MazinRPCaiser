package br.com.mnix.mazinrpcaiser.sample.simple.interfaces;

import br.com.mnix.mazinrpcaiser.common.DistributedVersion;

import javax.annotation.Nonnull;

/**
 * Created by mnix05 on 11/27/14.
 *
 * @author mnix05
 */
@DistributedVersion(of = ISubSample.class)
public interface IDistributedSubSample {
	@Nonnull String getFoo();
}
