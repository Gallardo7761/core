package net.miarma.api.common.exceptions;

/**
 * Excepción lanzada cuando se produce un conflicto en el estado actual del recurso.
 * Por ejemplo, cuando se intenta actualizar un recurso que ha sido modificado por otro usuario
 * o cuando se intenta realizar una operación que no es válida debido al estado actual del sistema.
 *
 * <p>Esta excepción es de tipo {@link RuntimeException}, por lo que no es necesario
 * declararla explícitamente en los métodos que la lanzan.</p>
 *
 * @author José Manuel Amador Gallardo
 */
public class ConflictException extends RuntimeException {

	/**
	 * Crea una nueva instancia de {@code ConflictException} con un mensaje descriptivo.
	 *
	 * @param message El mensaje que describe el error.
	 */
	public ConflictException(String message) {
		super(message);
	}

	/**
	 * Crea una nueva instancia de {@code ConflictException} con un mensaje y una causa.
	 *
	 * @param message El mensaje que describe el error.
	 * @param cause La causa original de esta excepción.
	 */
	public ConflictException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Crea una nueva instancia de {@code ConflictException} con una causa.
	 *
	 * @param cause La causa original de esta excepción.
	 */
	public ConflictException(Throwable cause) {
		super(cause);
	}

}
