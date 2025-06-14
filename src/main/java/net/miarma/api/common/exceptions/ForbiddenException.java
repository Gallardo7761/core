package net.miarma.api.common.exceptions;

/**
 * Excepción lanzada cuando se intenta acceder a un recurso o realizar una operación
 * que no está permitida para el usuario actual. Por ejemplo, cuando un usuario intenta
 * acceder a un recurso que requiere permisos especiales o cuando intenta realizar una
 * acción que no está autorizada.
 *
 * <p>Esta excepción es de tipo {@link RuntimeException}, por lo que no es necesario
 * declararla explícitamente en los métodos que la lanzan.</p>
 *
 * @author José Manuel Amador Gallardo
 */
public class ForbiddenException extends RuntimeException {

	/**
	 * Crea una nueva instancia de {@code ForbiddenException} con un mensaje descriptivo.
	 *
	 * @param message El mensaje que describe el error.
	 */
	public ForbiddenException(String message) {
		super(message);
	}

	/**
	 * Crea una nueva instancia de {@code ForbiddenException} con un mensaje y una causa.
	 *
	 * @param message El mensaje que describe el error.
	 * @param cause La causa original de esta excepción.
	 */
	public ForbiddenException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Crea una nueva instancia de {@code ForbiddenException} con una causa.
	 *
	 * @param cause La causa original de esta excepción.
	 */
	public ForbiddenException(Throwable cause) {
		super(cause);
	}

}
