package br.com.mnix.mazinrpcaiser.sample.concurrent;

import java.io.Serializable;

/**
 * Created by mnix05 on 12/4/14.
 *
 * @author mnix05
 */
public interface ISample extends Serializable {
	void print(Integer times);
}
