package br.com.mnix.mazinrpcaiser.common.exception;

/**
 * Created by mnix05 on 11/20/14.
 *
 * @author mnix05
 */
public class IllegalDistributedTypeException extends RuntimeException {
	public IllegalDistributedTypeException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 2981257538920834101L;
}
