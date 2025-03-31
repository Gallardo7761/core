package net.miarma.core.sso.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.core.common.ConfigManager;
import net.miarma.core.common.db.DatabaseProvider;
import net.miarma.core.sso.api.SSOLogicRouter;

public class SSOLogicVerticle extends AbstractVerticle {
	ConfigManager configManager;
	
	@Override 
	public void start(Promise<Void> startPromise) {
		configManager = ConfigManager.getInstance();
		Pool pool = DatabaseProvider.createPool(vertx, configManager);
		Router router = Router.router(vertx);
		SSOLogicRouter.mount(router, vertx, pool);
				
		vertx.createHttpServer()
			.requestHandler(router)
			.listen(configManager.getIntProperty("sso.logic.port"), res -> {
				if (res.succeeded()) startPromise.complete();
                else startPromise.fail(res.cause());
			});
	}
}
