package net.miarma.api.common.exceptions;

/**
 * Excepción lanzada cuando se recibe un código de estado HTTP 418 (I'm a teapot).
 * Esta excepción indica que el servidor se niega a preparar café porque es una tetera.
 * Es una broma del protocolo HTTP y no debe ser utilizada en aplicaciones reales, sin embargo,
 * la uso como excepción cuando alguien accede a un recurso que no debería.
 *
 * <p>Esta excepción es de tipo {@link RuntimeException}, por lo que no es necesario
 * declararla explícitamente en los métodos que la lanzan.</p>
 *
 * @author José Manuel Amador Gallardo
 */
public class TeapotException extends RuntimeException {

	/**
	 * Crea una nueva instancia de {@code TeapotException} con un mensaje descriptivo.
	 *
	 * @param message El mensaje que describe el error.
	 */
	public TeapotException(String message) {
		super(message);
	}

	/**
	 * Crea una nueva instancia de {@code TeapotException} con un mensaje y una causa.
	 *
	 * @param message El mensaje que describe el error.
	 * @param cause La causa original de esta excepción.
	 */
	public TeapotException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Crea una nueva instancia de {@code TeapotException} con una causa.
	 *
	 * @param cause La causa original de esta excepción.
	 */
	public TeapotException(Throwable cause) {
		super(cause);
	}

}
