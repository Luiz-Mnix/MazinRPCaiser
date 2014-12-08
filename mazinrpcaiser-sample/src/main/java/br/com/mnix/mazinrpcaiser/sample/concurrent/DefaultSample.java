package br.com.mnix.mazinrpcaiser.sample.concurrent;

import br.com.mnix.mazinrpcaiser.common.DefaultImplementation;

import javax.annotation.Nonnull;

/**
 * Created by mnix05 on 12/4/14.
 *
 * @author mnix05
 */
@DefaultImplementation
public class DefaultSample implements ISample {
	private static final long serialVersionUID = -4454972345939692935L;
	@Nonnull private final String mValue;
	@Nonnull public String getValue() {
		return mValue;
	}

	public DefaultSample(@Nonnull String value) {
		mValue = value;
	}

	@Override
	public void print(Integer times) {
		for(int idx = times; idx > 0; --idx) {
			System.out.println(this + ": " + getValue());

			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {}
		}
	}
}
