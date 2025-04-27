package net.miarma.api.common.exceptions;

public class TeapotException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public TeapotException(String message) {
		super(message);
	}
	
	public TeapotException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TeapotException(Throwable cause) {
		super(cause);
	}

}
