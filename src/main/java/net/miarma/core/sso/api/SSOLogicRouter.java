package net.miarma.core.sso.api;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.core.common.middlewares.AuthGuard;
import net.miarma.core.sso.handlers.LogicHandler;

public class SSOLogicRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		LogicHandler hLogic = new LogicHandler(vertx);
		
		router.route().handler(BodyHandler.create());
		router.post(SSOEndpoints.LOGIN).handler(hLogic::login);
		router.get(SSOEndpoints.USER_INFO).handler(AuthGuard.check()).handler(hLogic::getInfo);
        router.post(SSOEndpoints.REGISTER).handler(hLogic::register);
        router.post(SSOEndpoints.CHANGE_PASSWORD).handler(AuthGuard.check()).handler(hLogic::changePassword);
        router.get(SSOEndpoints.VALIDATE_TOKEN).handler(hLogic::validateToken);
		router.get(SSOEndpoints.USER_EXISTS).handler(AuthGuard.check()).handler(hLogic::exists);
		router.get(SSOEndpoints.USER_STATUS).handler(AuthGuard.check()).handler(hLogic::getStatus);
		router.put(SSOEndpoints.USER_STATUS).handler(AuthGuard.admin()).handler(hLogic::updateStatus);
		router.get(SSOEndpoints.USER_ROLE).handler(AuthGuard.check()).handler(hLogic::getRole);
		router.put(SSOEndpoints.USER_ROLE).handler(AuthGuard.admin()).handler(hLogic::updateRole);
		router.get(SSOEndpoints.USER_BY_EMAIL).handler(AuthGuard.check()).handler(hLogic::getByEmail);
		router.get(SSOEndpoints.USER_BY_USERNAME).handler(AuthGuard.check()).handler(hLogic::getByUserName);
		router.get(SSOEndpoints.USER_AVATAR).handler(AuthGuard.check()).handler(hLogic::getAvatar);
	}
}
