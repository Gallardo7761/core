package net.miarma.api.common.exceptions;

public class InternalServerErrorException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InternalServerErrorException(String message) {
		super(message);
	}

	public InternalServerErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public InternalServerErrorException(Throwable cause) {
		super(cause);
	}

}
