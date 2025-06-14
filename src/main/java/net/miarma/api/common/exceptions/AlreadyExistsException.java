package net.miarma.api.common.exceptions;

/**
 * Excepción lanzada cuando se intenta crear o registrar un recurso
 * que ya existe en el sistema. Por ejemplo, un usuario con un email duplicado
 * o un identificador ya registrado.
 *
 * <p>Esta excepción es de tipo {@link RuntimeException}, por lo que no es necesario
 * declararla explícitamente en los métodos que la lanzan.</p>
 *
 * @author José Manuel Amador Gallardo
 */
public class AlreadyExistsException extends RuntimeException {

	/**
	 * Crea una nueva instancia de {@code AlreadyExistsException} con un mensaje descriptivo.
	 *
	 * @param message El mensaje que describe el error.
	 */
	public AlreadyExistsException(String message) {
		super(message);
	}

	/**
	 * Crea una nueva instancia de {@code AlreadyExistsException} con un mensaje y una causa.
	 *
	 * @param message El mensaje que describe el error.
	 * @param cause La causa original de esta excepción.
	 */
	public AlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
}
