package net.miarma.api.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiResponse;
import net.miarma.api.common.http.ApiStatus;

/**
 * Clase de utilidad para enviar respuestas JSON.
 * @author José Manuel Amador Gallardo
 */
public class JsonUtil {
	public static <T> void sendJson(RoutingContext ctx, ApiStatus status, T data) {
	    sendJson(ctx, status, data, status.getDefaultMessage());
	}

	public static <T> void sendJson(RoutingContext ctx, ApiStatus status, T data, String message) {
	    ctx.response().putHeader("Content-Type", "application/json").setStatusCode(status.getCode());

	    if (data instanceof JsonObject || data instanceof JsonArray) {
	        JsonObject response = new JsonObject()
	            .put("status", status.getCode())
	            .put("message", message)
	            .put("data", data);
	        ctx.response().end(response.encode());
	    } else {
	        ctx.response().end(Constants.GSON.toJson(new ApiResponse<>(status, message, data)));
	    }
	}

}
