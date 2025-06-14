package net.miarma.api.common.exceptions;

/**
 * Excepción lanzada cuando un servicio no está disponible temporalmente.
 * Esto puede ocurrir, por ejemplo, cuando el servidor está en mantenimiento
 * o cuando hay problemas de conectividad con un servicio externo.
 *
 * <p>Esta excepción es de tipo {@link RuntimeException}, por lo que no es necesario
 * declararla explícitamente en los métodos que la lanzan.</p>
 *
 * @author José Manuel Amador Gallardo
 */
public class ServiceUnavailableException extends RuntimeException {

	/**
	 * Crea una nueva instancia de {@code ServiceUnavailableException} con un mensaje descriptivo.
	 *
	 * @param message El mensaje que describe el error.
	 */
	public ServiceUnavailableException(String message) {
		super(message);
	}

	/**
	 * Crea una nueva instancia de {@code ServiceUnavailableException} con un mensaje y una causa.
	 *
	 * @param message El mensaje que describe el error.
	 * @param cause La causa original de esta excepción.
	 */
	public ServiceUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Crea una nueva instancia de {@code ServiceUnavailableException} con una causa.
	 *
	 * @param cause La causa original de esta excepción.
	 */
	public ServiceUnavailableException(Throwable cause) {
		super(cause);
	}

}
