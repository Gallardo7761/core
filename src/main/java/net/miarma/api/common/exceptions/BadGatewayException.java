package net.miarma.api.common.exceptions;

public class BadGatewayException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public BadGatewayException(String message) {
		super(message);
	}
	
	public BadGatewayException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BadGatewayException(Throwable cause) {
		super(cause);
	}

}
