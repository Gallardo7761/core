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
import net.miarma.api.microservices.core.handlers.FileLogicHandler;
import net.miarma.api.microservices.core.handlers.ScreenshotHandler;
import net.miarma.api.microservices.core.handlers.UserLogicHandler;

public class CoreLogicRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		UserLogicHandler hUserLogic = new UserLogicHandler(vertx);
		FileLogicHandler hFileLogic = new FileLogicHandler(vertx);
		ScreenshotHandler hScreenshot = new ScreenshotHandler(vertx);
        
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
		
		router.post(CoreEndpoints.LOGIN).handler(hUserLogic::login);
		router.get(CoreEndpoints.USER_INFO).handler(AuthGuard.check()).handler(hUserLogic::getInfo);
        router.post(CoreEndpoints.REGISTER).handler(hUserLogic::register);
        router.post(CoreEndpoints.CHANGE_PASSWORD).handler(AuthGuard.check()).handler(hUserLogic::changePassword);
        router.post(CoreEndpoints.LOGIN_VALID).handler(hUserLogic::loginValidate);
        router.get(CoreEndpoints.VALIDATE_TOKEN).handler(hUserLogic::validateToken);
        router.get(CoreEndpoints.REFRESH_TOKEN).handler(hUserLogic::refreshToken);
		
        router.get(CoreEndpoints.USER_EXISTS).handler(AuthGuard.check()).handler(hUserLogic::exists);
		router.get(CoreEndpoints.USER_STATUS).handler(AuthGuard.check()).handler(hUserLogic::getStatus);
		router.put(CoreEndpoints.USER_STATUS).handler(AuthGuard.admin()).handler(hUserLogic::updateStatus);
		router.get(CoreEndpoints.USER_ROLE).handler(AuthGuard.check()).handler(hUserLogic::getRole);
		router.put(CoreEndpoints.USER_ROLE).handler(AuthGuard.admin()).handler(hUserLogic::updateRole);
		router.get(CoreEndpoints.USER_AVATAR).handler(AuthGuard.check()).handler(hUserLogic::getAvatar);
		
		router.get(CoreEndpoints.FILE_DOWNLOAD).handler(AuthGuard.check()).handler(hFileLogic::downloadFile);
		router.get(CoreEndpoints.USER_FILES).handler(AuthGuard.check()).handler(hFileLogic::getUserFiles);
		
		router.get(CoreEndpoints.SCREENSHOT).handler(hScreenshot::getScreenshot);
	}
}
