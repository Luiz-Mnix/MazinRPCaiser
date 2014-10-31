package br.com.mnix.mazinrpcaiser.common;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by mnix05 on 10/31/14.
 *
 * @author mnix05
 */
public interface IActionMetadata extends Serializable{
	@Nonnull String getSessionId();
	@Nonnull String getTopicId();
}
