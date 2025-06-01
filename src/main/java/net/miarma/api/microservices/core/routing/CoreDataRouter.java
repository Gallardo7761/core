package net.miarma.api.microservices.core.routing;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.http.ApiResponse;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.middlewares.AuthGuard;
import net.miarma.api.common.security.SusPather;
import net.miarma.api.microservices.core.handlers.FileDataHandler;
import net.miarma.api.microservices.core.handlers.UserDataHandler;

public class CoreDataRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		UserDataHandler hUserData = new UserDataHandler(pool);
		FileDataHandler hFileData = new FileDataHandler(pool);
			
		router.route().handler(BodyHandler.create());
		
		// teapot :P
		router.route().handler(ctx -> {
			String path = ctx.request().path();
			ApiResponse<JsonObject> response = new ApiResponse<JsonObject>(ApiStatus.IM_A_TEAPOT, "I'm a teapot", null);
			JsonObject jsonResponse = new JsonObject()
					.put("status", response.getStatus())
					.put("message", response.getMessage());
			if(SusPather.isSusPath(path)) {
				ctx.response()
					.setStatusCode(response.getStatus())
					.putHeader("Content-Type", "application/json")
					.end(jsonResponse.encode());
			} else {
				ctx.next();
			}
		});
		
		router.get(CoreEndpoints.USERS).handler(AuthGuard.admin()).handler(hUserData::getAll);
		router.get(CoreEndpoints.USER).handler(AuthGuard.admin()).handler(hUserData::getById);
		router.post(CoreEndpoints.USERS).handler(hUserData::create);
		router.put(CoreEndpoints.USER).handler(AuthGuard.admin()).handler(hUserData::update);
		router.delete(CoreEndpoints.USER).handler(AuthGuard.admin()).handler(hUserData::delete);
		
		router.get(CoreEndpoints.FILES).handler(AuthGuard.check()).handler(hFileData::getAll);
		router.get(CoreEndpoints.FILE).handler(AuthGuard.check()).handler(hFileData::getById);
		router.post(CoreEndpoints.FILE_UPLOAD).handler(AuthGuard.check()).handler(hFileData::create);
		router.put(CoreEndpoints.FILE).handler(AuthGuard.check()).handler(hFileData::update);
		router.delete(CoreEndpoints.FILE).handler(AuthGuard.check()).handler(hFileData::delete);
		
	}
}
