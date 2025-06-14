package net.miarma.api.common.exceptions;

/**
 * Excepción lanzada cuando la solicitud no puede ser procesada debido a
 * errores de validación o problemas con los datos proporcionados.
 * Esto puede incluir datos faltantes, formatos incorrectos o valores no válidos.
 *
 * <p>Esta excepción es de tipo {@link RuntimeException}, por lo que no es necesario
 * declararla explícitamente en los métodos que la lanzan.</p>
 *
 * @author José Manuel Amador Gallardo
 */
public class UnprocessableEntityException extends RuntimeException {

	/**
	 * Crea una nueva instancia de {@code UnprocessableEntityException} con un mensaje descriptivo.
	 *
	 * @param message El mensaje que describe el error.
	 */
	public UnprocessableEntityException(String message) {
		super(message);
	}

	/**
	 * Crea una nueva instancia de {@code UnprocessableEntityException} con un mensaje y una causa.
	 *
	 * @param message El mensaje que describe el error.
	 * @param cause La causa original de esta excepción.
	 */
	public UnprocessableEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Crea una nueva instancia de {@code UnprocessableEntityException} con una causa.
	 *
	 * @param cause La causa original de esta excepción.
	 */
	public UnprocessableEntityException(Throwable cause) {
		super(cause);
	}

}
