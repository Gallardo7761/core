package net.miarma.api.microservices.mpaste.routing;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.http.ApiResponse;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.common.security.SusPather;
import net.miarma.api.microservices.mpaste.handlers.PasteDataHandler;
import net.miarma.api.util.RateLimiter;

public class MPasteDataRouter {
	private static RateLimiter limiter = new RateLimiter();
	
	public static void mount(Router router, Vertx vertx, Pool pool) {
		PasteDataHandler hPasteData = new PasteDataHandler(pool);
		
		router.route().handler(BodyHandler.create());
		// teapot :P
		router.route().handler(ctx -> {
			String path = ctx.request().path();
			ApiResponse<JsonObject> response = new ApiResponse<>(ApiStatus.IM_A_TEAPOT, "I'm a teapot", null);
			JsonObject jsonResponse = new JsonObject().put("status", response.getStatus()).put("message",
					response.getMessage());
			if (SusPather.isSusPath(path)) {
				ctx.response().setStatusCode(response.getStatus()).putHeader("Content-Type", "application/json")
						.end(jsonResponse.encode());
			} else {
				ctx.next();
			}
		});
		
		router.get(MPasteEndpoints.PASTES).handler(hPasteData::getAll);
		router.post(MPasteEndpoints.PASTES).handler(ctx -> {
			String ip = ctx.request().remoteAddress().host();
			if (!limiter.allow(ip)) {
		    	ApiResponse<JsonObject> response = new ApiResponse<>(ApiStatus.TOO_MANY_REQUESTS, "Too many requests", null);
				JsonObject jsonResponse = new JsonObject().put("status", response.getStatus()).put("message",
						response.getMessage());
				ctx.response().setStatusCode(response.getStatus()).putHeader("Content-Type", "application/json")
					.end(jsonResponse.encode());
		    }
		    hPasteData.create(ctx);
		});
		router.get(MPasteEndpoints.PASTE).handler(hPasteData::getById);
		router.delete(MPasteEndpoints.PASTE).handler(hPasteData::delete);
	}
}
