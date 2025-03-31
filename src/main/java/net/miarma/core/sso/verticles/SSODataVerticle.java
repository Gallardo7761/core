package net.miarma.core.sso.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import net.miarma.core.common.ConfigManager;
import net.miarma.core.common.Constants;
import net.miarma.core.common.db.DatabaseProvider;
import net.miarma.core.sso.api.SSODataRouter;
import net.miarma.core.sso.entities.UserEntity;
import net.miarma.core.sso.services.SSOService;

public class SSODataVerticle extends AbstractVerticle  {
	ConfigManager configManager;
	SSOService ssoService;
	
	@Override 
	public void start(Promise<Void> startPromise) {
		configManager = ConfigManager.getInstance();
		Pool pool = DatabaseProvider.createPool(vertx, configManager);
		ssoService = new SSOService(pool);
		Router router = Router.router(vertx);
		SSODataRouter.mount(router, vertx, pool);
		registerAuthConsumer();
				
		vertx.createHttpServer()
			.requestHandler(router)
			.listen(configManager.getIntProperty("sso.data.port"), res -> {
				if (res.succeeded()) startPromise.complete();
                else startPromise.fail(res.cause());
			});
	}
	
	private void registerAuthConsumer() {
	    vertx.eventBus().consumer(Constants.AUTH_EVENT_BUS, message -> {
	        JsonObject body = (JsonObject) message.body();
	        String action = body.getString("action");

	        switch (action) {
	            case "login":
	                ssoService.login(body.getString("email"), body.getString("password"), body.getBoolean("keepLoggedIn"), ar -> {
	                    if (ar.succeeded()) {
	                        message.reply(ar.result());
	                    } else {
	                        message.fail(401, ar.cause().getMessage());
	                    }
	                });
	                break;

	            case "register":
	                UserEntity user = new UserEntity();
	                user.setUser_name(body.getString("userName"));
	                user.setEmail(body.getString("email"));
	                user.setDisplay_name(body.getString("displayName"));
	                user.setPassword(body.getString("password"));

	                ssoService.register(user, ar -> {
	                    if (ar.succeeded()) {
	                        message.reply("User registered successfully");
	                    } else {
	                        message.fail(400, ar.cause().getMessage());
	                    }
	                });
	                break;

	            case "changePassword":
	                ssoService.changePassword(body.getInteger("userId"), body.getString("newPassword"), ar -> {
	                    if (ar.succeeded()) {
	                        message.reply("Password changed successfully");
	                    } else {
	                        message.fail(400, ar.cause().getMessage());
	                    }
	                });
	                break;

	            case "validateToken":
	                boolean isValid = ssoService.validateToken(body.getString("token"));
	                message.reply(isValid);
	                break;
	                
	            default:
	                message.fail(400, "Acción desconocida: " + action);
	                break;
	        }
	    });
	}
}
