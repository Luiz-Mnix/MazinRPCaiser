package br.com.mnix.mazinrpcaiser.server.service;

/**
 * Created by mnix05 on 11/6/14.
 *
 * @author mnix05
 */
public class RequestHasNoServiceException extends Exception {
	private static final long serialVersionUID = -112128908156740124L;

	public RequestHasNoServiceException(String message) {
		super(message);
	}

}
