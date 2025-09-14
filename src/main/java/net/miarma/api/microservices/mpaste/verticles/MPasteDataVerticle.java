package net.miarma.api.microservices.mpaste.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.db.DatabaseProvider;
import net.miarma.api.microservices.mpaste.routing.MPasteDataRouter;
import net.miarma.api.microservices.mpaste.services.PasteService;
import net.miarma.api.util.EventBusUtil;
import net.miarma.api.util.RouterUtil;

public class MPasteDataVerticle extends AbstractVerticle {
	private ConfigManager configManager;
	private PasteService pasteService;
	
	@Override
    public void start(Promise<Void> startPromise) {
        configManager = ConfigManager.getInstance();
        Pool pool = DatabaseProvider.createPool(vertx, configManager);
        
        pasteService = new PasteService(pool);
        
        Router router = Router.router(vertx);
        RouterUtil.attachLogger(router);
        MPasteDataRouter.mount(router, vertx, pool);
        registerLogicVerticleConsumer();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(configManager.getIntProperty("mpaste.data.port"), res -> {
                if (res.succeeded()) startPromise.complete();
                else startPromise.fail(res.cause());
            });
    }

    private void registerLogicVerticleConsumer() {
        vertx.eventBus().consumer(Constants.MPASTE_EVENT_BUS, message -> {
            JsonObject body = (JsonObject) message.body();
            String action = body.getString("action");

            switch (action) {
            	case "getByKey" -> {
            		pasteService.getByKey(body.getString("key"), body.getString("password"))
	            		.onSuccess(paste -> message.reply(new JsonObject(Constants.GSON.toJson(paste))))
	                    .onFailure(EventBusUtil.fail(message));
            	}
            }
        });
    }
}
