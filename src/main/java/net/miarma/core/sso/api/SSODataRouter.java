package net.miarma.core.sso.api;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.core.common.middlewares.AuthGuard;
import net.miarma.core.sso.handlers.UserHandler;

public class SSODataRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		UserHandler user = new UserHandler(pool);
		
		router.route().handler(BodyHandler.create());
		router.get(SSOEndpoints.USERS).handler(AuthGuard.admin()).handler(user::getAll);
		router.get(SSOEndpoints.USER).handler(AuthGuard.admin()).handler(user::getById);
		router.post(SSOEndpoints.USER).handler(user::create);
		router.put(SSOEndpoints.USER).handler(AuthGuard.admin()).handler(user::update);
		router.delete(SSOEndpoints.USER).handler(AuthGuard.admin()).handler(user::delete);
		router.get(SSOEndpoints.USER_STATUS).handler(AuthGuard.check()).handler(user::getStatus);
		router.put(SSOEndpoints.USER_STATUS).handler(AuthGuard.admin()).handler(user::updateStatus);
		router.get(SSOEndpoints.USER_ROLE).handler(AuthGuard.check()).handler(user::getRole);
		router.put(SSOEndpoints.USER_ROLE).handler(AuthGuard.admin()).handler(user::updateRole);
		router.get(SSOEndpoints.USER_BY_EMAIL).handler(AuthGuard.check()).handler(user::getByEmail);
		router.get(SSOEndpoints.USER_BY_USERNAME).handler(AuthGuard.check()).handler(user::getByUserName);
		router.get(SSOEndpoints.USER_INFO).handler(AuthGuard.check()).handler(user::getInfo);
		router.get(SSOEndpoints.USER_EXISTS).handler(AuthGuard.check()).handler(user::exists);
		router.get(SSOEndpoints.USER_AVATAR).handler(AuthGuard.check()).handler(user::getAvatar);
	}
}
