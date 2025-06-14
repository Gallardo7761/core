package net.miarma.api.common.exceptions;

/**
 * Excepción lanzada cuando un usuario no está autorizado para realizar una acción
 * o acceder a un recurso específico. Esto puede ocurrir, por ejemplo, cuando
 * se intenta acceder a un recurso sin las credenciales adecuadas o sin los permisos necesarios.
 *
 * <p>Esta excepción es de tipo {@link RuntimeException}, por lo que no es necesario
 * declararla explícitamente en los métodos que la lanzan.</p>
 *
 * @author José Manuel Amador Gallardo
 */
public class UnauthorizedException extends RuntimeException {

	/**
	 * Crea una nueva instancia de {@code UnauthorizedException} con un mensaje descriptivo.
	 *
	 * @param message El mensaje que describe el error.
	 */
	public UnauthorizedException(String message) {
		super(message);
	}

	/**
	 * Crea una nueva instancia de {@code UnauthorizedException} con un mensaje y una causa.
	 *
	 * @param message El mensaje que describe el error.
	 * @param cause La causa original de esta excepción.
	 */
	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Crea una nueva instancia de {@code UnauthorizedException} con una causa.
	 *
	 * @param cause La causa original de esta excepción.
	 */
	public UnauthorizedException(Throwable cause) {
		super(cause);
	}

}
