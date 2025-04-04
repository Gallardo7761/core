package net.miarma.api.common.exceptions;

public class ServiceUnavailableException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ServiceUnavailableException(String message) {
		super(message);
	}
	
	public ServiceUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ServiceUnavailableException(Throwable cause) {
		super(cause);
	}

}
