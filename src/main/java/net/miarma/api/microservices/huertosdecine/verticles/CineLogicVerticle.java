package net.miarma.api.microservices.huertosdecine.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.db.DatabaseProvider;
import net.miarma.api.microservices.huertosdecine.routing.CineLogicRouter;
import net.miarma.api.util.RouterUtil;

public class CineLogicVerticle extends AbstractVerticle {
    private ConfigManager configManager;

    @Override
    public void start(Promise<Void> startPromise) {
        configManager = ConfigManager.getInstance();
        Pool pool = DatabaseProvider.createPool(vertx, configManager);
        Router router = Router.router(vertx);
        RouterUtil.attachLogger(router);
        CineLogicRouter.mount(router, vertx, pool);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(configManager.getIntProperty("cine.logic.port"), res -> {
                if (res.succeeded()) startPromise.complete();
                else startPromise.fail(res.cause());
            });
    }
}
