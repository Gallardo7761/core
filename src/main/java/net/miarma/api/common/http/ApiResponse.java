package net.miarma.api.common.http;

public class ApiResponse<T> {
    private final int status;
    private final String message;
    private final T data;

    public ApiResponse(ApiStatus status, String message, T data) {
        this.status = status.getCode();
        this.message = message;
        this.data = data;
    }

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

