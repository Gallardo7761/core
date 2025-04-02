package net.miarma.api.core.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.Constants.CoreUserGlobalStatus;
import net.miarma.api.common.Constants.CoreUserRole;
import net.miarma.api.common.db.DatabaseProvider;
import net.miarma.api.core.api.CoreDataRouter;
import net.miarma.api.core.entities.UserEntity;
import net.miarma.api.core.services.FileService;
import net.miarma.api.core.services.UserService;

public class CoreDataVerticle extends AbstractVerticle  {
	private ConfigManager configManager;
	private UserService userService;
	private FileService fileService;
	
	
	@Override 
	public void start(Promise<Void> startPromise) {
		configManager = ConfigManager.getInstance();
		Pool pool = DatabaseProvider.createPool(vertx, configManager);
		userService = new UserService(pool);
		fileService = new FileService(pool);
		Router router = Router.router(vertx);
		CoreDataRouter.mount(router, vertx, pool);
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
	                userService.login(body.getString("email"), body.getString("password"), body.getBoolean("keepLoggedIn"), ar -> {
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

	                userService.register(user, ar -> {
	                    if (ar.succeeded()) {
	                        message.reply("User registered successfully");
	                    } else {
	                        message.fail(400, ar.cause().getMessage());
	                    }
	                });
	                break;

	            case "changePassword":
	                userService.changePassword(body.getInteger("userId"), body.getString("newPassword"), ar -> {
	                    if (ar.succeeded()) {
	                        message.reply("Password changed successfully");
	                    } else {
	                        message.fail(400, ar.cause().getMessage());
	                    }
	                });
	                break;

	            case "validateToken":
	                boolean isValid = userService.validateToken(body.getString("token"));
	                message.reply(isValid);
	                break;
	                
	            case "getInfo":
	                userService.getById(body.getInteger("userId"), ar -> {
	                    if (ar.succeeded() && ar.result() != null) {
	                        message.reply(new JsonObject(ar.result().encode()));
	                    } else {
	                        message.fail(404, "User not found");
	                    }
	                });
	                break;

	            case "userExists":
	                userService.getById(body.getInteger("userId"), ar -> {
	                    boolean exists = ar.succeeded() && ar.result() != null;
	                    message.reply(new JsonObject().put("user_id", body.getInteger("userId")).put("exists", exists));
	                });
	                break;
	                
	            case "getById":
	                userService.getById(body.getInteger("userId"), ar -> {
	                    if (ar.succeeded() && ar.result() != null) {
	                        message.reply(new JsonObject(ar.result().encode()));
	                    } else {
	                        message.fail(404, "User not found");
	                    }
	                });
	                break;

	            case "getByEmail":
	                userService.getByEmail(body.getString("email"), ar -> {
	                    if (ar.succeeded() && ar.result() != null) {
	                        message.reply(new JsonObject(ar.result().encode()));
	                    } else {
	                        message.fail(404, "Not found");
	                    }
	                });
	                break;

	            case "getByUserName":
	                userService.getByUserName(body.getString("userName"), ar -> {
	                    if (ar.succeeded() && ar.result() != null) {
	                        message.reply(new JsonObject(ar.result().encode()));
	                    } else {
	                        message.fail(404, "Not found");
	                    }
	                });
	                break;

	            case "getStatus":
	                userService.getById(body.getInteger("userId"), ar -> {
	                    if (ar.succeeded() && ar.result() != null) {
	                        JsonObject response = new JsonObject()
	                            .put("user_id", ar.result().getUser_id())
	                            .put("status", ar.result().getGlobal_status());
	                        message.reply(response);
	                    } else {
	                        message.fail(404, "User not found");
	                    }
	                });
	                break;

	            case "getRole":
	                userService.getById(body.getInteger("userId"), ar -> {
	                    if (ar.succeeded() && ar.result() != null) {
	                        JsonObject response = new JsonObject()
	                            .put("user_id", ar.result().getUser_id())
	                            .put("role", ar.result().getRole());
	                        message.reply(response);
	                    } else {
	                        message.fail(404, "User not found");
	                    }
	                });
	                break;

	            case "getAvatar":
	                userService.getById(body.getInteger("userId"), ar -> {
	                    if (ar.succeeded() && ar.result() != null) {
	                        JsonObject response = new JsonObject()
	                            .put("user_id", ar.result().getUser_id())
	                            .put("avatar", ar.result().getAvatar());
	                        message.reply(response);
	                    } else {
	                        message.fail(404, "User not found");
	                    }
	                });
	                break;
	                
	            case "updateStatus":
	                userService.updateStatus(body.getInteger("userId"), 
	                		CoreUserGlobalStatus.fromInt(body.getInteger("status")), ar -> {
	                    if (ar.succeeded()) {
	                        message.reply("Status updated successfully");
	                    } else {
	                        message.fail(400, ar.cause().getMessage());
	                    }
	                });
	                break;
	                
	            case "updateRole":
	            	userService.updateRole(body.getInteger("userId"), 
	            			CoreUserRole.fromInt(body.getInteger("role")), ar -> {
	                    if (ar.succeeded()) {
	                        message.reply("Role updated successfully");
	                    } else {
	                        message.fail(400, ar.cause().getMessage());
	                    }
	                });
	                break;
	                
	            case "getUserFiles":
	                fileService.getUserFiles(body.getInteger("userId"), ar -> {
	                    if (ar.succeeded() && ar.result() != null) {
	                        message.reply(Constants.GSON.toJson(ar.result(), ar.result().getClass()));
	                    } else {
	                        message.fail(404, "The user has no files");
	                    }
	                });
	                break;
	                
	            case "uploadFile":
	                fileService.create(body, ar -> {
	                    if (ar.succeeded()) {
	                        message.reply("File uploaded successfully");
	                    } else {
	                        message.fail(400, ar.cause().getMessage());
	                    }
	                });
	                break;
	                
	            case "downloadFile":
	            	fileService.downloadFile(body.getInteger("fileId"), ar -> {
	                    if (ar.succeeded()) {
	                        message.reply(ar.result());
	                    } else {
	                        message.fail(404, "File not found");
	                    }
	                });               	            		                
	                break;
	                
	            default:
	                message.fail(400, "Unknown action: " + action);
	                break;
	        }
	    });
	}
}
