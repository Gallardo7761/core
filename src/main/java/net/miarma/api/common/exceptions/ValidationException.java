package net.miarma.api.common.exceptions;

public class ValidationException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7635266685058098986L;

	public ValidationException(String json) {
        super(json);
    }
}
