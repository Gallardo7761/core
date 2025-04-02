package net.miarma.api.core.api;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.middlewares.AuthGuard;
import net.miarma.api.core.handlers.DataHandler;

public class CoreDataRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		DataHandler hData = new DataHandler(pool);
		
		router.route().handler(BodyHandler.create());
		router.get(CoreEndpoints.USERS).handler(AuthGuard.admin()).handler(hData::getAll);
		router.get(CoreEndpoints.USER).handler(AuthGuard.admin()).handler(hData::getById);
		router.post(CoreEndpoints.USER).handler(hData::create);
		router.put(CoreEndpoints.USER).handler(AuthGuard.admin()).handler(hData::update);
		router.delete(CoreEndpoints.USER).handler(AuthGuard.admin()).handler(hData::delete);
	}
}
