package net.miarma.api.common.exceptions;

/**
 * Excepción lanzada cuando ocurre un error interno en el servidor que impide
 * completar la solicitud. Esto puede deberse a problemas de configuración, errores
 * en el código del servidor o fallos inesperados.
 *
 * <p>Esta excepción es de tipo {@link RuntimeException}, por lo que no es necesario
 * declararla explícitamente en los métodos que la lanzan.</p>
 *
 * @author José Manuel Amador Gallardo
 */
public class InternalServerErrorException extends RuntimeException {

	/**
	 * Crea una nueva instancia de {@code InternalServerErrorException} con un mensaje descriptivo.
	 *
	 * @param message El mensaje que describe el error.
	 */
	public InternalServerErrorException(String message) {
		super(message);
	}

	/**
	 * Crea una nueva instancia de {@code InternalServerErrorException} con un mensaje y una causa.
	 *
	 * @param message El mensaje que describe el error.
	 * @param cause La causa original de esta excepción.
	 */
	public InternalServerErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Crea una nueva instancia de {@code InternalServerErrorException} con una causa.
	 *
	 * @param cause La causa original de esta excepción.
	 */
	public InternalServerErrorException(Throwable cause) {
		super(cause);
	}

}
