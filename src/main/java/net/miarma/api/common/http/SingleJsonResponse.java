package net.miarma.api.common.http;

/**
 * Representa una respuesta JSON que contiene un único mensaje.
 * Esta clase se utiliza para encapsular una respuesta simple en formato JSON.
 *
 * @param <T> el tipo del mensaje que se envía en la respuesta
 * @author José Manuel Amador Gallardo
 */
public record SingleJsonResponse<T>(T message) {
	public static <T> SingleJsonResponse<T> of(T message) {
		return new SingleJsonResponse<>(message);
	}
}
