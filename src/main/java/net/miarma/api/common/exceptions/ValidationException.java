package net.miarma.api.common.exceptions;

/**
 * Excepción lanzada cuando los datos proporcionados no cumplen con las reglas de validación
 * establecidas en el sistema. Esto puede ocurrir, por ejemplo, cuando se envían datos
 * incompletos o incorrectos en una solicitud.
 *
 * <p>Esta excepción es de tipo {@link RuntimeException}, por lo que no es necesario
 * declararla explícitamente en los métodos que la lanzan.</p>
 *
 * @author José Manuel Amador Gallardo
 */
public class ValidationException extends RuntimeException {

    /**
     * Crea una nueva instancia de {@code ValidationException} con un mensaje descriptivo.
     *
     * @param json El JSON que describe el error de validación.
     */
	public ValidationException(String json) {
        super(json);
    }
}
