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
                case "login" -> userService.login(body.getString("email"), body.getString("password"), body.getBoolean("keepLoggedIn"))
                    .onSuccess(message::reply)
                    .onFailure(err -> message.fail(401, err.getMessage()));

                case "register" -> {
                    UserEntity user = new UserEntity();
                    user.setUser_name(body.getString("userName"));
                    user.setEmail(body.getString("email"));
                    user.setDisplay_name(body.getString("displayName"));
                    user.setPassword(body.getString("password"));

                    userService.register(user)
                        .onSuccess(res -> message.reply("User registered successfully"))
                        .onFailure(err -> message.fail(400, err.getMessage()));
                }

                case "changePassword" -> userService.changePassword(body.getInteger("userId"), body.getString("newPassword"))
                    .onSuccess(res -> message.reply("Password changed successfully"))
                    .onFailure(err -> message.fail(400, err.getMessage()));

                case "validateToken" -> {
                    boolean isValid = userService.validateToken(body.getString("token"));
                    message.reply(isValid);
                }

                case "getInfo", "getById" -> userService.getById(body.getInteger("userId"))
                    .onSuccess(user -> message.reply(new JsonObject(user.encode())))
                    .onFailure(err -> message.fail(404, "User not found"));

                case "userExists" -> userService.getById(body.getInteger("userId"))
                    .onSuccess(user -> message.reply(new JsonObject().put("user_id", body.getInteger("userId")).put("exists", user != null)))
                    .onFailure(err -> message.reply(new JsonObject().put("user_id", body.getInteger("userId")).put("exists", false)));

                case "getByEmail" -> userService.getByEmail(body.getString("email"))
                    .onSuccess(user -> message.reply(new JsonObject(user.encode())))
                    .onFailure(err -> message.fail(404, "Not found"));

                case "getByUserName" -> userService.getByUserName(body.getString("userName"))
                    .onSuccess(user -> message.reply(new JsonObject(user.encode())))
                    .onFailure(err -> message.fail(404, "Not found"));

                case "getStatus" -> userService.getById(body.getInteger("userId"))
                    .onSuccess(user -> {
                        JsonObject response = new JsonObject()
                            .put("user_id", user.getUser_id())
                            .put("status", user.getGlobal_status());
                        message.reply(response);
                    })
                    .onFailure(err -> message.fail(404, "User not found"));

                case "getRole" -> userService.getById(body.getInteger("userId"))
                    .onSuccess(user -> {
                        JsonObject response = new JsonObject()
                            .put("user_id", user.getUser_id())
                            .put("role", user.getRole());
                        message.reply(response);
                    })
                    .onFailure(err -> message.fail(404, "User not found"));

                case "getAvatar" -> userService.getById(body.getInteger("userId"))
                    .onSuccess(user -> {
                        JsonObject response = new JsonObject()
                            .put("user_id", user.getUser_id())
                            .put("avatar", user.getAvatar());
                        message.reply(response);
                    })
                    .onFailure(err -> message.fail(404, "User not found"));

                case "updateStatus" -> userService.updateStatus(body.getInteger("userId"),
                        CoreUserGlobalStatus.fromInt(body.getInteger("status")))
                    .onSuccess(res -> message.reply("Status updated successfully"))
                    .onFailure(err -> message.fail(400, err.getMessage()));

                case "updateRole" -> userService.updateRole(body.getInteger("userId"),
                        CoreUserRole.fromInt(body.getInteger("role")))
                    .onSuccess(res -> message.reply("Role updated successfully"))
                    .onFailure(err -> message.fail(400, err.getMessage()));

                case "getUserFiles" -> fileService.getUserFiles(body.getInteger("userId"))
                    .onSuccess(files -> message.reply(Constants.GSON.toJson(files, files.getClass())))
                    .onFailure(err -> message.fail(404, "The user has no files"));

                case "uploadFile" -> fileService.create(body)
                    .onSuccess(res -> message.reply("File uploaded successfully"))
                    .onFailure(err -> message.fail(400, err.getMessage()));

                case "downloadFile" -> fileService.downloadFile(body.getInteger("fileId"))
                    .onSuccess(message::reply)
                    .onFailure(err -> message.fail(404, "File not found"));

                default -> message.fail(400, "Unknown action: " + action);
            }
        });
    }
}