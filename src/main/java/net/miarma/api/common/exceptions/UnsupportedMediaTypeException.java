package net.miarma.api.common.exceptions;

public class UnsupportedMediaTypeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public UnsupportedMediaTypeException(String message) {
		super(message);
	}
	
	public UnsupportedMediaTypeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public UnsupportedMediaTypeException(Throwable cause) {
		super(cause);
	}

}
