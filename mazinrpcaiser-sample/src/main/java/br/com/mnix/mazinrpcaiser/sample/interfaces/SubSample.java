package br.com.mnix.mazinrpcaiser.sample.interfaces;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/18/14.
 *
 * @author mnix05
 */
public class SubSample implements Serializable {
	private static final long serialVersionUID = -5319548722094671083L;

	@Nonnull
	private final String mFoo;

	public SubSample(@Nonnull String foo) {
		mFoo = foo;
	}

	@Nonnull public String getFoo() {
		return mFoo;
	}
}
