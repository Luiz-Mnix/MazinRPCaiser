package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.server.data.IContext;

import java.io.Serializable;

/**
 * Created by mnix05 on 11/11/14.
 *
 * @author mnix05
 */
public interface ITranslator {
	Serializable translate(Serializable data, IContext context) throws TranslationException;
}
