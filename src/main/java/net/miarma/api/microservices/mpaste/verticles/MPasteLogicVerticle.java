package net.miarma.api.microservices.mpaste.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.db.DatabaseProvider;
import net.miarma.api.microservices.mpaste.routing.MPasteLogicRouter;
import net.miarma.api.util.RouterUtil;

public class MPasteLogicVerticle extends AbstractVerticle {
	private ConfigManager configManager;
	
	@Override
	public void start(Promise<Void> startPromise) {
		configManager = ConfigManager.getInstance();
		Pool pool = DatabaseProvider.createPool(vertx, configManager);
		Router router = Router.router(vertx);
		RouterUtil.attachLogger(router);
		MPasteLogicRouter.mount(router, vertx, pool);
		
		vertx.createHttpServer()
			.requestHandler(router)
			.listen(configManager.getIntProperty("mpaste.logic.port"), res -> {
				if (res.succeeded()) startPromise.complete();
	            else startPromise.fail(res.cause());
		});
	}
}
