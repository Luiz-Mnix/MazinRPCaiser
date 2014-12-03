package br.com.mnix.mazinrpcaiser.sample.simple.interfaces;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by mnix05 on 11/27/14.
 *
 * @author mnix05
 */
public interface ISubSample extends Serializable {
	@Nonnull
	String getFoo();
}
