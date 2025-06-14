package net.miarma.api.common.exceptions;

/**
 * Excepción lanzada cuando un recurso solicitado no se encuentra en el sistema.
 * Esto puede ocurrir, por ejemplo, cuando se intenta acceder a un recurso
 * que no existe o ha sido eliminado.
 *
 * <p>Esta excepción es de tipo {@link RuntimeException}, por lo que no es necesario
 * declararla explícitamente en los métodos que la lanzan.</p>
 *
 * @author José Manuel Amador Gallardo
 */
public class NotFoundException extends RuntimeException {

	/**
	 * Crea una nueva instancia de {@code NotFoundException} con un mensaje descriptivo.
	 *
	 * @param message El mensaje que describe el error.
	 */
	public NotFoundException(String message) {
		super(message);
	}

	/**
	 * Crea una nueva instancia de {@code NotFoundException} con un mensaje y una causa.
	 *
	 * @param message El mensaje que describe el error.
	 * @param cause La causa original de esta excepción.
	 */
	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Crea una nueva instancia de {@code NotFoundException} con una causa.
	 *
	 * @param cause La causa original de esta excepción.
	 */
	public NotFoundException(Throwable cause) {
		super(cause);
	}
}
