package br.com.mnix.mazinrpcaiser.sample.distributed;

import br.com.mnix.mazinrpcaiser.common.DistributedVersion;

/**
 * Created by mnix05 on 11/28/14.
 *
 * @author mnix05
 */
@DistributedVersion(of = ISample.class)
public interface IDistributedSample {
	Integer getCode();
	void setCode(Integer code);
}
