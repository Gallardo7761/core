package net.miarma.api.common.http;

public record SingleJsonResponse<T>(T message) {
	public static <T> SingleJsonResponse<T> of(T message) {
		return new SingleJsonResponse<>(message);
	}
}
