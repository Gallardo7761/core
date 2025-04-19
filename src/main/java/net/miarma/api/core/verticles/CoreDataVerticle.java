package net.miarma.api.core.verticles;

import java.util.HashMap;
import java.util.Map;

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
import net.miarma.api.core.entities.UserEntity;
import net.miarma.api.core.routing.CoreDataRouter;
import net.miarma.api.core.services.FileService;
import net.miarma.api.core.services.UserService;
import net.miarma.api.util.EventBusUtil;
import net.miarma.api.util.RouterUtil;

@SuppressWarnings("unused")
public class CoreDataVerticle extends AbstractVerticle {
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
        RouterUtil.attachLogger(router);
        CoreDataRouter.mount(router, vertx, pool);
        registerLogicVerticleConsumer();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(configManager.getIntProperty("sso.data.port"), res -> {
                if (res.succeeded()) startPromise.complete();
                else startPromise.fail(res.cause());
            });
    }

    private void registerLogicVerticleConsumer() {
        vertx.eventBus().consumer(Constants.AUTH_EVENT_BUS, message -> {
            JsonObject body = (JsonObject) message.body();
            String action = body.getString("action");

            switch (action) {
                case "login" -> {
                	String email = body.getString("email");
                	String password = body.getString("password");
                	boolean keepLoggedIn = body.getBoolean("keepLoggedIn", false);
                	
                	userService.login(email, password, keepLoggedIn)
		                .onSuccess(message::reply)
		                .onFailure(EventBusUtil.fail(message));
                }

                case "register" -> {
                    UserEntity user = new UserEntity();
                    user.setUser_name(body.getString("userName"));
                    user.setEmail(body.getString("email"));
                    user.setDisplay_name(body.getString("displayName"));
                    user.setPassword(body.getString("password"));

                    userService.register(user)
	                    .onSuccess(message::reply)
	                    .onFailure(EventBusUtil.fail(message));
                }

                case "changePassword" -> {
                	Integer userId = body.getInteger("userId");
                	String newPassword = body.getString("newPassword");
                	
                	userService.changePassword(userId, newPassword)
		                .onSuccess(message::reply)
		                .onFailure(EventBusUtil.fail(message));
                }

                case "validateToken" -> {
                	String token = body.getString("token");
                	
                    userService.validateToken(token)
	            		.onSuccess(message::reply)
		                .onFailure(EventBusUtil.fail(message));
                }

                case "getInfo", "getById" -> {
                	Integer userId = body.getInteger("userId");
                	
                	userService.getById(userId)
	                    .onSuccess(message::reply)
	                    .onFailure(EventBusUtil.fail(message));
                }

                case "userExists" -> {
                    Integer userId = body.getInteger("userId");

                    userService.getById(userId)
                        .onSuccess(user -> {
                            Map<String, Object> result = new HashMap<>();
                            result.put("user_id", userId);
                            result.put("exists", user != null);
                            message.reply(result);
                        })
                        .onFailure(EventBusUtil.fail(message));
                }

                case "getByEmail" -> userService.getByEmail(body.getString("email"))
                    .onSuccess(message::reply)
                    .onFailure(EventBusUtil.fail(message));

                case "getByUserName" -> userService.getByUserName(body.getString("userName"))
                    .onSuccess(message::reply)
                    .onFailure(EventBusUtil.fail(message));

                case "getStatus" -> userService.getById(body.getInteger("userId"))
                    .onSuccess(user -> {
                        Map<String, Object> result = new HashMap<>();
                        result.put("user_id", user.getUser_id());
                        result.put("status", user.getGlobal_status());
                        message.reply(result);
                    })
                    .onFailure(EventBusUtil.fail(message));

                case "getRole" -> userService.getById(body.getInteger("userId"))
                    .onSuccess(user -> {
                        Map<String, Object> result = new HashMap<>();
                        result.put("user_id", user.getUser_id());
                        result.put("role", user.getRole());
                        message.reply(result);
                    })
                    .onFailure(EventBusUtil.fail(message));

                case "getAvatar" -> userService.getById(body.getInteger("userId"))
                    .onSuccess(user -> {
                        Map<String, Object> result = new HashMap<>();
                        result.put("user_id", user.getUser_id());
                        result.put("avatar", user.getAvatar());
                        message.reply(result);
                    })
                    .onFailure(EventBusUtil.fail(message));

                case "updateStatus" -> userService.updateStatus(
                        body.getInteger("userId"),
                        CoreUserGlobalStatus.fromInt(body.getInteger("status")))
                    .onSuccess(res -> message.reply("Status updated successfully"))
                    .onFailure(EventBusUtil.fail(message));

                case "updateRole" -> userService.updateRole(
                        body.getInteger("userId"),
                        CoreUserRole.fromInt(body.getInteger("role")))
                    .onSuccess(res -> message.reply("Role updated successfully"))
                    .onFailure(EventBusUtil.fail(message));

                case "getUserFiles" -> fileService.getUserFiles(body.getInteger("userId"))
                    .onSuccess(message::reply)
                    .onFailure(EventBusUtil.fail(message));

                case "downloadFile" -> fileService.downloadFile(body.getInteger("fileId"))
                    .onSuccess(message::reply)
                    .onFailure(EventBusUtil.fail(message));
                
                case "getUserById" -> userService.getById(body.getInteger("userId"))
					.onSuccess(user -> {
						String userJson = Constants.GSON.toJson(user);
						message.reply(new JsonObject(userJson));
					})
					.onFailure(EventBusUtil.fail(message));

                default -> EventBusUtil.fail(message);
            }
        });
    }
}