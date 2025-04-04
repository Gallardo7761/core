package net.miarma.api.core.handlers;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.util.JsonUtil;

public class FileLogicHandler {

    private final Vertx vertx;

    public FileLogicHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    private boolean validateAuth(RoutingContext ctx, JsonObject request) {
        String authHeader = ctx.request().getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            JsonUtil.sendJson(ctx, ApiStatus.UNAUTHORIZED, null, "Unauthorized");
            return false;
        }

        String token = authHeader.substring(7);
        int userId = JWTManager.getInstance().getUserId(token);

        if (userId <= 0) {
            JsonUtil.sendJson(ctx, ApiStatus.UNAUTHORIZED, null, "Invalid token");
            return false;
        }

        request.put("userId", userId);
        return true;
    }

    public void getUserFiles(RoutingContext ctx) {
        JsonObject request = new JsonObject().put("action", "getUserFiles");
        if (!validateAuth(ctx, request)) return;

        vertx.eventBus().request(Constants.CORE_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            } else {
                JsonUtil.sendJson(ctx, ApiStatus.NOT_FOUND, null, "The user has no files");
            }
        });
    }

    public void uploadFile(RoutingContext ctx) {
        JsonObject request = new JsonObject()
            .put("action", "uploadFile")
            .put("file", ctx.body().asJsonObject());

        if (!validateAuth(ctx, request)) return;

        vertx.eventBus().request(Constants.CORE_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                JsonUtil.sendJson(ctx, ApiStatus.CREATED, ar.result().body());
            } else {
                JsonUtil.sendJson(ctx, ApiStatus.INTERNAL_SERVER_ERROR, null, "Error uploading file");
            }
        });
    }

    public void downloadFile(RoutingContext ctx) {
        JsonObject request = new JsonObject()
            .put("action", "downloadFile")
            .put("fileId", Integer.parseInt(ctx.pathParam("file_id")));

        if (!validateAuth(ctx, request)) return;

        vertx.eventBus().request(Constants.CORE_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            } else {
                JsonUtil.sendJson(ctx, ApiStatus.NOT_FOUND, null, "Error downloading file");
            }
        });
    }
} 
