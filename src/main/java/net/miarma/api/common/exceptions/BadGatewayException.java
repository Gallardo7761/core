package net.miarma.api.common.exceptions;

/**
 * Excepción lanzada cuando un servidor actúa como puerta de enlace o proxy
 * y recibe una respuesta inválida o no válida de un servidor ascendente.
 * Esto puede ocurrir, por ejemplo, cuando el servidor ascendente está inactivo
 * o devuelve un error inesperado.
 *
 * <p>Esta excepción es de tipo {@link RuntimeException}, por lo que no es necesario
 * declararla explícitamente en los métodos que la lanzan.</p>
 *
 * @author José Manuel Amador Gallardo
 */
public class BadGatewayException extends RuntimeException {

	/**
	 * Crea una nueva instancia de {@code BadGatewayException} con un mensaje descriptivo.
	 *
	 * @param message El mensaje que describe el error.
	 */
	public BadGatewayException(String message) {
		super(message);
	}

	/**
	 * Crea una nueva instancia de {@code BadGatewayException} con un mensaje y una causa.
	 *
	 * @param message El mensaje que describe el error.
	 * @param cause La causa original de esta excepción.
	 */
	public BadGatewayException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Crea una nueva instancia de {@code BadGatewayException} con una causa.
	 *
	 * @param cause La causa original de esta excepción.
	 */
	public BadGatewayException(Throwable cause) {
		super(cause);
	}

}
