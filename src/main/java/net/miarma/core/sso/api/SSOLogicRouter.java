package net.miarma.core.sso.api;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.core.sso.handlers.AuthHandler;

public class SSOLogicRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		AuthHandler auth = new AuthHandler(vertx);
		
		router.route().handler(BodyHandler.create());
		router.post(SSOEndpoints.LOGIN).handler(auth::login);
        router.post(SSOEndpoints.REGISTER).handler(auth::register);
        router.post(SSOEndpoints.CHANGE_PASSWORD).handler(auth::changePassword);
        router.get(SSOEndpoints.VALIDATE_TOKEN).handler(auth::validateToken);
	}
}
