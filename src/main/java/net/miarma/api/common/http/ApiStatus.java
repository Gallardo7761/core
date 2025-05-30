package net.miarma.api.common.http;

import net.miarma.api.common.exceptions.*;

public enum ApiStatus {
    OK(200),
    CREATED(201),
    ACCEPTED(202),
    NO_CONTENT(204),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    CONFLICT(409),
    IM_A_TEAPOT(418),
    UNPROCESSABLE_ENTITY(422),
    UNSUPPORTED_MEDIA_TYPE(415),
    INTERNAL_SERVER_ERROR(500),
    SERVICE_UNAVAILABLE(503);

    private final int code;

    ApiStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
    
    public String getDefaultMessage() {
    	return switch (this) {
			case OK -> "OK";
			case CREATED -> "Created";
			case ACCEPTED -> "Accepted";
			case NO_CONTENT -> "No Content";
			case BAD_REQUEST -> "Bad Request";
			case UNAUTHORIZED -> "Unauthorized";
			case FORBIDDEN -> "Forbidden";
			case NOT_FOUND -> "Not Found";
			case CONFLICT -> "Conflict";
			case IM_A_TEAPOT -> "The server refuses the attempt to brew coffee with a teapot";
			case UNPROCESSABLE_ENTITY -> "Unprocessable Entity";
			case UNSUPPORTED_MEDIA_TYPE -> "Unsupported Media Type";
			case INTERNAL_SERVER_ERROR -> "Internal Server Error";
			case SERVICE_UNAVAILABLE -> "Service Unavailable";
		};
    }
    
    public static ApiStatus fromException(Throwable t) {
		if (t instanceof NotFoundException) return ApiStatus.NOT_FOUND;
		if (t instanceof BadRequestException) return ApiStatus.BAD_REQUEST;
		if (t instanceof UnauthorizedException) return ApiStatus.UNAUTHORIZED;
		if (t instanceof ForbiddenException) return ApiStatus.FORBIDDEN;
		if (t instanceof ConflictException) return ApiStatus.CONFLICT;
		if (t instanceof TeapotException) return ApiStatus.IM_A_TEAPOT;
		if (t instanceof ServiceUnavailableException) return ApiStatus.SERVICE_UNAVAILABLE;
		if (t instanceof UnprocessableEntityException) return ApiStatus.UNPROCESSABLE_ENTITY;
		if (t instanceof UnsupportedMediaTypeException) return ApiStatus.UNSUPPORTED_MEDIA_TYPE;
		if (t instanceof ValidationException) return ApiStatus.BAD_REQUEST;
		return ApiStatus.INTERNAL_SERVER_ERROR;
	}

	public static ApiStatus fromCode(int code) {
		return switch (code) {
			case 200 -> OK;
			case 201 -> CREATED;
			case 202 -> ACCEPTED;
			case 204 -> NO_CONTENT;
			case 400 -> BAD_REQUEST;
			case 401 -> UNAUTHORIZED;
			case 403 -> FORBIDDEN;
			case 404 -> NOT_FOUND;
			case 409 -> CONFLICT;
			case 418 -> IM_A_TEAPOT;
			case 422 -> UNPROCESSABLE_ENTITY;
			case 415 -> UNSUPPORTED_MEDIA_TYPE;
			case 500 -> INTERNAL_SERVER_ERROR;
			case 503 -> SERVICE_UNAVAILABLE;
			default -> null;
		};
	}
}

