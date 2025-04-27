package net.miarma.api.miarmacraft.routing;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.http.ApiResponse;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.middlewares.AuthGuard;
import net.miarma.api.common.security.SusPather;
import net.miarma.api.miarmacraft.handlers.PlayerLogicHandler;

public class MMCLogicRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		PlayerLogicHandler hPlayerLogic = new PlayerLogicHandler(vertx);
		
		router.route().handler(BodyHandler.create());
		// teapot :P
		router.route().handler(ctx -> {
			String path = ctx.request().path();
			ApiResponse<JsonObject> response = new ApiResponse<JsonObject>(ApiStatus.IM_A_TEAPOT, "I'm a teapot", null);
			JsonObject jsonResponse = new JsonObject().put("status", response.getStatus()).put("message",
					response.getMessage());
			if (SusPather.isSusPath(path)) {
				ctx.response().setStatusCode(response.getStatus()).putHeader("Content-Type", "application/json")
						.end(jsonResponse.encode());
			} else {
				ctx.next();
			}
		});
		
		router.post(MMCEndpoints.LOGIN).handler(hPlayerLogic::login);
		router.get(MMCEndpoints.PLAYER_STATUS).handler(AuthGuard.admin()).handler(hPlayerLogic::getStatus);
		router.put(MMCEndpoints.PLAYER_STATUS).handler(AuthGuard.admin()).handler(hPlayerLogic::updateStatus);
		router.get(MMCEndpoints.PLAYER_ROLE).handler(AuthGuard.admin()).handler(hPlayerLogic::getRole);
		router.put(MMCEndpoints.PLAYER_ROLE).handler(AuthGuard.admin()).handler(hPlayerLogic::updateRole);
		router.get(MMCEndpoints.PLAYER_AVATAR).handler(AuthGuard.check()).handler(hPlayerLogic::getAvatar);
		router.put(MMCEndpoints.PLAYER_AVATAR).handler(AuthGuard.check()).handler(hPlayerLogic::updateAvatar);
	}
}
