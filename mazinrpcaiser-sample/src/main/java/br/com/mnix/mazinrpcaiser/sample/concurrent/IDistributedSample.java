package br.com.mnix.mazinrpcaiser.sample.concurrent;

import br.com.mnix.mazinrpcaiser.common.DistributedVersion;

/**
 * Created by mnix05 on 12/4/14.
 *
 * @author mnix05
 */
@DistributedVersion(of = ISample.class)
public interface IDistributedSample {
	void print(Integer times);
}
