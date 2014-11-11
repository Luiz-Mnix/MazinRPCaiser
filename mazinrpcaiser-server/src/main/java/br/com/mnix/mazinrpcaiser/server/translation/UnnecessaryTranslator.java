package br.com.mnix.mazinrpcaiser.server.translation;

import br.com.mnix.mazinrpcaiser.server.data.IContext;

import java.io.Serializable;

/**
 * Created by mnix05 on 11/11/14.
 *
 * @author mnix05
 */
@Translator(of = {Number.class, Character.class, String.class, Class.class})
public class UnnecessaryTranslator implements ITranslator {
	@Override
	public Serializable translate(Serializable data, IContext context) {
		return data;
	}
}
