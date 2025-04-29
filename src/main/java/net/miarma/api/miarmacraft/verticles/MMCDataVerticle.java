package net.miarma.api.miarmacraft.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.Constants.MMCUserRole;
import net.miarma.api.common.Constants.MMCUserStatus;
import net.miarma.api.common.db.DatabaseProvider;
import net.miarma.api.miarmacraft.routing.MMCDataRouter;
import net.miarma.api.miarmacraft.services.ModService;
import net.miarma.api.miarmacraft.services.PlayerService;
import net.miarma.api.util.EventBusUtil;
import net.miarma.api.util.RouterUtil;

public class MMCDataVerticle extends AbstractVerticle {
	private ConfigManager configManager;
	private PlayerService playerService;
	
	@Override
	public void start(Promise<Void> startPromise) {
		configManager = ConfigManager.getInstance();
		Pool pool = DatabaseProvider.createPool(vertx, configManager);
		
		playerService = new PlayerService(pool);
		
		Router router = Router.router(vertx);
		RouterUtil.attachLogger(router);
		MMCDataRouter.mount(router, vertx, pool);
		registerLogicVerticleConsumer();
		
		vertx.createHttpServer()
			.requestHandler(router)
			.listen(configManager.getIntProperty("mmc.data.port"), res -> {
                if (res.succeeded()) startPromise.complete();
                else startPromise.fail(res.cause());
            });
	} 
	
	private void registerLogicVerticleConsumer() {
		vertx.eventBus().consumer(Constants.MMC_EVENT_BUS, message -> {
			JsonObject body = (JsonObject) message.body();
			String action = body.getString("action");
			
			switch(action) {
				case "login" -> {
					String email = body.getString("email", null);
                    String userName = body.getString("userName", null);
                    String password = body.getString("password");
                    boolean keepLoggedIn = body.getBoolean("keepLoggedIn", false);

                    playerService.login(email != null ? email : userName, password, keepLoggedIn)
                        .onSuccess(message::reply)
                        .onFailure(EventBusUtil.fail(message));
				}
			
				case "getStatus" -> {
					playerService.getStatus(body.getInteger("playerId"))
						.onSuccess(player -> message.reply(new JsonObject(Constants.GSON.toJson(player))))
						.onFailure(EventBusUtil::fail);
				}
				
				case "getRole" -> {
					playerService.getRole(body.getInteger("playerId"))
						.onSuccess(role -> message.reply(new JsonObject(Constants.GSON.toJson(role))))
						.onFailure(EventBusUtil::fail);
				}
				
				case "getAvatar" -> {
					playerService.getAvatar(body.getInteger("playerId"))
						.onSuccess(avatar -> message.reply(new JsonObject(Constants.GSON.toJson(avatar))))
						.onFailure(EventBusUtil::fail);
				}
				
				case "updateStatus" -> {
					MMCUserStatus status = MMCUserStatus.fromInt(body.getInteger("status"));
					playerService.updateStatus(body.getInteger("playerId"), status)
						.onSuccess(result -> message.reply(new JsonObject(Constants.GSON.toJson(result))))
						.onFailure(EventBusUtil::fail);
				}
				
				case "updateRole" -> {
					MMCUserRole role = MMCUserRole.fromInt(body.getInteger("role"));
					playerService.updateRole(body.getInteger("playerId"), role)
						.onSuccess(result -> message.reply(new JsonObject(Constants.GSON.toJson(result))))
						.onFailure(EventBusUtil::fail);
				}
				
				case "updateAvatar" -> {
					String avatar = body.getString("avatar");
					playerService.updateAvatar(body.getInteger("playerId"), avatar)
						.onSuccess(result -> message.reply(new JsonObject(Constants.GSON.toJson(result))))
						.onFailure(EventBusUtil::fail);
				}
				
				case "playerExists" -> {
					playerService.playerExists(body.getInteger("playerId"))
						.onSuccess(exists -> message.reply(new JsonObject(Constants.GSON.toJson(exists))))
						.onFailure(EventBusUtil::fail);
				}
				
				case "getInfo" -> {
					playerService.getInfo(body.getString("token"))
						.onSuccess(player -> message.reply(new JsonObject(Constants.GSON.toJson(player))))
						.onFailure(EventBusUtil::fail);
				}
		
				default -> EventBusUtil.fail(message).handle(new IllegalArgumentException("Unknown action: " + action));
			}
		});
	}
}
