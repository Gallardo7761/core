package net.miarma.api.microservices.miarmacraft.routing;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.http.ApiResponse;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.middlewares.AuthGuard;
import net.miarma.api.common.security.SusPather;
import net.miarma.api.microservices.miarmacraft.handlers.ModDataHandler;
import net.miarma.api.microservices.miarmacraft.handlers.PlayerDataHandler;

public class MMCDataRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		ModDataHandler hModData = new ModDataHandler(pool);
		PlayerDataHandler hPlayerData = new PlayerDataHandler(pool);
		
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
		
		router.get(MMCEndpoints.MODS).handler(AuthGuard.check()).handler(hModData::getAll);
		router.get(MMCEndpoints.MOD).handler(AuthGuard.check()).handler(hModData::getById);
		router.post(MMCEndpoints.MODS).handler(BodyHandler.create().setBodyLimit(100 * 1024 * 1024)).handler(AuthGuard.admin()).handler(hModData::create); 
		router.put(MMCEndpoints.MOD).handler(AuthGuard.admin()).handler(hModData::update);
		router.delete(MMCEndpoints.MOD).handler(AuthGuard.admin()).handler(hModData::delete);
		
		router.get(MMCEndpoints.PLAYERS).handler(AuthGuard.admin()).handler(hPlayerData::getAll);
		router.post(MMCEndpoints.PLAYERS).handler(AuthGuard.admin()).handler(hPlayerData::create);
		router.put(MMCEndpoints.PLAYER).handler(AuthGuard.admin()).handler(hPlayerData::update);
		router.delete(MMCEndpoints.PLAYER).handler(AuthGuard.admin()).handler(hPlayerData::delete);
		router.get(MMCEndpoints.PLAYER).handler(AuthGuard.admin()).handler(hPlayerData::getById);
	}
}
