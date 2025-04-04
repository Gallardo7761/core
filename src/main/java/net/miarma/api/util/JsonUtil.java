package net.miarma.api.util;

import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiResponse;
import net.miarma.api.common.http.ApiStatus;

public class JsonUtil {
	public static <T> void sendJson(RoutingContext ctx, ApiStatus status, T data) {
	    sendJson(ctx, status, data, status.getDefaultMessage());
	}

	public static <T> void sendJson(RoutingContext ctx, ApiStatus status, T data, String message) {
	    ctx.response()
	        .putHeader("Content-Type", "application/json")
	        .setStatusCode(status.getCode())
	        .end(Constants.GSON.toJson(new ApiResponse<>(status, message, data)));
	}
}
