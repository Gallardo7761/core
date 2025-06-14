package net.miarma.api.common.exceptions;

/**
 * Excepción lanzada cuando se recibe una solicitud con datos inválidos o mal formados.
 * Por ejemplo, cuando un campo requerido está vacío o un valor no cumple con las restricciones
 * del sistema.
 *
 * <p>Esta excepción es de tipo {@link RuntimeException}, por lo que no es necesario
 * declararla explícitamente en los métodos que la lanzan.</p>
 *
 * @author José Manuel Amador Gallardo
 */
public class BadRequestException extends RuntimeException {

	/**
	 * Crea una nueva instancia de {@code BadRequestException} con un mensaje descriptivo.
	 *
	 * @param message El mensaje que describe el error.
	 */
	public BadRequestException(String message) {
		super(message);
	}

	/**
	 * Crea una nueva instancia de {@code BadRequestException} con un mensaje y una causa.
	 *
	 * @param message El mensaje que describe el error.
	 * @param cause La causa original de esta excepción.
	 */
	public BadRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Crea una nueva instancia de {@code BadRequestException} con una causa.
	 *
	 * @param cause La causa original de esta excepción.
	 */
	public BadRequestException(Throwable cause) {
		super(cause);
	}

}
