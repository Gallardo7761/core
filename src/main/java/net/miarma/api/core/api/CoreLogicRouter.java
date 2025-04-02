package net.miarma.api.core.api;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.middlewares.AuthGuard;
import net.miarma.api.core.handlers.LogicHandler;

public class CoreLogicRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		LogicHandler hLogic = new LogicHandler(vertx);
		
		router.route().handler(BodyHandler.create());
		router.post(CoreEndpoints.LOGIN).handler(hLogic::login);
		router.get(CoreEndpoints.USER_INFO).handler(AuthGuard.check()).handler(hLogic::getInfo);
        router.post(CoreEndpoints.REGISTER).handler(hLogic::register);
        router.post(CoreEndpoints.CHANGE_PASSWORD).handler(AuthGuard.check()).handler(hLogic::changePassword);
        router.get(CoreEndpoints.VALIDATE_TOKEN).handler(hLogic::validateToken);
		router.get(CoreEndpoints.USER_EXISTS).handler(AuthGuard.check()).handler(hLogic::exists);
		router.get(CoreEndpoints.USER_STATUS).handler(AuthGuard.check()).handler(hLogic::getStatus);
		router.put(CoreEndpoints.USER_STATUS).handler(AuthGuard.admin()).handler(hLogic::updateStatus);
		router.get(CoreEndpoints.USER_ROLE).handler(AuthGuard.check()).handler(hLogic::getRole);
		router.put(CoreEndpoints.USER_ROLE).handler(AuthGuard.admin()).handler(hLogic::updateRole);
		router.get(CoreEndpoints.USER_BY_EMAIL).handler(AuthGuard.check()).handler(hLogic::getByEmail);
		router.get(CoreEndpoints.USER_BY_USERNAME).handler(AuthGuard.check()).handler(hLogic::getByUserName);
		router.get(CoreEndpoints.USER_AVATAR).handler(AuthGuard.check()).handler(hLogic::getAvatar);
	}
}
