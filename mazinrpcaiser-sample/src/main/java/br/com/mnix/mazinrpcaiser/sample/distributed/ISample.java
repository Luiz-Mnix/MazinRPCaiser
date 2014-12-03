package br.com.mnix.mazinrpcaiser.sample.distributed;

import java.io.Serializable;

/**
 * Created by mnix05 on 11/28/14.
 *
 * @author mnix05
 */
public interface ISample extends Serializable {
	Integer getCode();
	void setCode(Integer code);
}
