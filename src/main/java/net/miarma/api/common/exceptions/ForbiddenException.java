package net.miarma.api.common.exceptions;

public class ForbiddenException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ForbiddenException(String message) {
		super(message);
	}
	
	public ForbiddenException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ForbiddenException(Throwable cause) {
		super(cause);
	}

}
