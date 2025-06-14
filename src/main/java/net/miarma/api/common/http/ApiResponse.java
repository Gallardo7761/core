package net.miarma.api.common.http;

/**
 * Clase genérica para representar una respuesta de la API.
 * <p>
 * Esta clase encapsula el estado de la respuesta, un mensaje descriptivo y los datos devueltos.
 * Se utiliza para estandarizar las respuestas de la API y facilitar el manejo de errores y datos.
 * <p>
 * Ejemplo de uso:
 * <pre>
 *     ApiResponse<MyDataType> response = new ApiResponse<>(ApiStatus.SUCCESS, "Data retrieved successfully", myData);
 * </pre>
 * @see ApiStatus
 *
 * @param <T> Tipo de dato que contendrá la respuesta.
 *
 * @author José Manuel Amador Gallardo
 */
public class ApiResponse<T> {
    private final int status;
    private final String message;
    private final T data;

    /**
     * Constructor para crear una respuesta de la API con un estado, mensaje y datos.
     *
     * @param status El estado de la respuesta, representado por un código.
     * @param message Un mensaje descriptivo de la respuesta.
     * @param data Los datos devueltos en la respuesta, puede ser null si no hay datos.
     */
    public ApiResponse(ApiStatus status, String message, T data) {
        this.status = status.getCode();
        this.message = message;
        this.data = data;
    }

    /**
     * Constructor para crear una respuesta de la API con un estado y mensaje, sin datos.
     *
     * @param status El estado de la respuesta, representado por un código.
     * @param message Un mensaje descriptivo de la respuesta.
     */
    public ApiResponse(ApiStatus status, String message) {
        this(status, message, null);
    }

	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public T getData() {
		return data;
	}
	    
}

