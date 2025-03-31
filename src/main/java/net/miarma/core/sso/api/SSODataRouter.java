package net.miarma.core.sso.api;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.core.common.middlewares.AuthGuard;
import net.miarma.core.sso.handlers.DataHandler;

public class SSODataRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		DataHandler hData = new DataHandler(pool);
		
		router.route().handler(BodyHandler.create());
		router.get(SSOEndpoints.USERS).handler(AuthGuard.admin()).handler(hData::getAll);
		router.get(SSOEndpoints.USER).handler(AuthGuard.admin()).handler(hData::getById);
		router.post(SSOEndpoints.USER).handler(hData::create);
		router.put(SSOEndpoints.USER).handler(AuthGuard.admin()).handler(hData::update);
		router.delete(SSOEndpoints.USER).handler(AuthGuard.admin()).handler(hData::delete);
	}
}
