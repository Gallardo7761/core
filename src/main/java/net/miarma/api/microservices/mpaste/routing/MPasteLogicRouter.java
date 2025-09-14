package net.miarma.api.microservices.mpaste.routing;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.http.ApiResponse;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.security.SusPather;
import net.miarma.api.microservices.mpaste.handlers.PasteLogicHandler;

public class MPasteLogicRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		PasteLogicHandler hPasteLogic = new PasteLogicHandler(vertx);
		
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
		
		router.get(MPasteEndpoints.PASTE_BY_KEY).handler(hPasteLogic::getByKey);
	}
}
