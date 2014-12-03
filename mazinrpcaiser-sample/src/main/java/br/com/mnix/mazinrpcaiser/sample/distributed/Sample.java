package br.com.mnix.mazinrpcaiser.sample.distributed;

import br.com.mnix.mazinrpcaiser.common.DefaultImplementation;

/**
 * Created by mnix05 on 11/28/14.
 *
 * @author mnix05
 */
@DefaultImplementation
public class Sample implements ISample {
	private static final long serialVersionUID = -2414381514562638743L;

	private int mCode;

	@Override
	public Integer getCode() {
		return mCode;
	}

	@Override
	public void setCode(Integer code) {
		mCode = code;
	}
}
