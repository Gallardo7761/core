package net.miarma.api.core.handlers;

import com.auth0.jwt.interfaces.DecodedJWT;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.core.entities.UserEntity;
import net.miarma.api.util.JsonUtil;

public class UserLogicHandler {

    private final Vertx vertx;

    public UserLogicHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    private void handleError(RoutingContext ctx, Throwable err) {
        ApiStatus status = ApiStatus.fromException(err);
        JsonUtil.sendJson(ctx, status, null, err.getMessage());
    }

    public void login(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();

        JsonObject request = new JsonObject()
    		.put("action", "login")
            .put("email", body.getString("email", null))
            .put("userName", body.getString("userName", null))
            .put("password", body.getString("password"))
            .put("keepLoggedIn", body.getBoolean("keepLoggedIn", false));

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                JsonObject result = (JsonObject) ar.result().body();
                result.put("tokenTime", System.currentTimeMillis());
                JsonUtil.sendJson(ctx, ApiStatus.OK, result);
            } else {
                handleError(ctx, ar.cause());
            }
        });
    }
    
    public void loginValidate(RoutingContext ctx) {
		JsonObject body = ctx.body().asJsonObject();

		JsonObject request = new JsonObject()
				.put("action", "loginValidate")
				.put("userId", body.getInteger("userId"))
				.put("password", body.getString("password"));

		vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) {
				JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			} else {
				handleError(ctx, ar.cause());
			}
		});
	}

    public void register(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();

        JsonObject request = new JsonObject()
                .put("action", "register")
                .put("userName", body.getString("userName"))
                .put("email", body.getString("email"))
                .put("displayName", body.getString("displayName"))
                .put("password", body.getString("password"));

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                JsonUtil.sendJson(ctx, ApiStatus.CREATED, null);
            } else {
                handleError(ctx, ar.cause());
            }
        });
    }

    public void changePassword(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();

        JsonObject request = new JsonObject()
                .put("action", "changePassword")
                .put("userId", body.getInteger("userId"))
                .put("newPassword", body.getString("newPassword"));

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null);
            } else {
                handleError(ctx, ar.cause());
            }
        });
    }

    public void validateToken(RoutingContext ctx) {
        String authHeader = ctx.request().getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            JsonObject request = new JsonObject()
                    .put("action", "validateToken")
                    .put("token", token);

            vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
                if (ar.succeeded() && Boolean.TRUE.equals(ar.result().body())) {
                    JsonUtil.sendJson(ctx, ApiStatus.OK, true, "Valid token");
                } else {
                    JsonUtil.sendJson(ctx, ApiStatus.UNAUTHORIZED, false, "Invalid token");
                }
            });
        } else {
            JsonUtil.sendJson(ctx, ApiStatus.BAD_REQUEST, null, "Missing or invalid Authorization header");
        }
    }
    
    public void refreshToken(RoutingContext ctx) {
        String tokenHeader = ctx.request().getHeader("Authorization");

        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            JsonUtil.sendJson(ctx, ApiStatus.UNAUTHORIZED, null, "Missing or invalid Authorization header");
            return;
        }

        String token = tokenHeader.substring("Bearer ".length());
        JWTManager jwt = JWTManager.getInstance();

        try {
            DecodedJWT decoded = jwt.decodeWithoutVerification(token);
            int userId = decoded.getClaim("userId").asInt();
            if (userId == -1) {
                JsonUtil.sendJson(ctx, ApiStatus.UNAUTHORIZED, null, "Invalid token");
                return;
            }

            vertx.eventBus().request(Constants.AUTH_EVENT_BUS, new JsonObject()
                .put("action", "getUserById")
                .put("userId", userId), ar -> {

                if (ar.succeeded()) {
                    JsonObject userJson = (JsonObject) ar.result().body();
                    UserEntity user = Constants.GSON.fromJson(userJson.encode(), UserEntity.class);
                    String newToken = jwt.generateToken(user, false);

                    JsonUtil.sendJson(ctx, ApiStatus.OK, new JsonObject().put("token", newToken));
                } else {
                    JsonUtil.sendJson(ctx, ApiStatus.UNAUTHORIZED, null, "User not found");
                }
            });

        } catch (Exception e) {
            JsonUtil.sendJson(ctx, ApiStatus.UNAUTHORIZED, null, "Invalid token");
        }
    }



    public void getInfo(RoutingContext ctx) {
        String authHeader = ctx.request().getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            JsonUtil.sendJson(ctx, ApiStatus.UNAUTHORIZED, null, "Unauthorized");
            return;
        }

        String token = authHeader.substring(7);
        int userId = net.miarma.api.common.security.JWTManager.getInstance().getUserId(token);

        if (userId <= 0) {
            JsonUtil.sendJson(ctx, ApiStatus.UNAUTHORIZED, null, "Invalid token");
            return;
        }

        JsonObject request = new JsonObject()
                .put("action", "getInfo")
                .put("userId", userId);

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            } else {
                handleError(ctx, ar.cause());
            }
        });
    }

    public void exists(RoutingContext ctx) {
        int userId = Integer.parseInt(ctx.pathParam("user_id"));

        JsonObject request = new JsonObject()
                .put("action", "userExists")
                .put("userId", userId);

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            } else {
                handleError(ctx, ar.cause());
            }
        });
    }

    public void getStatus(RoutingContext ctx) {
        int userId = Integer.parseInt(ctx.pathParam("user_id"));

        JsonObject request = new JsonObject()
                .put("action", "getStatus")
                .put("userId", userId);

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            } else {
                handleError(ctx, ar.cause());
            }
        });
    }

    public void getRole(RoutingContext ctx) {
        int userId = Integer.parseInt(ctx.pathParam("user_id"));

        JsonObject request = new JsonObject()
                .put("action", "getRole")
                .put("userId", userId);

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            } else {
                handleError(ctx, ar.cause());
            }
        });
    }

    public void getAvatar(RoutingContext ctx) {
        int userId = Integer.parseInt(ctx.pathParam("user_id"));

        JsonObject request = new JsonObject()
                .put("action", "getAvatar")
                .put("userId", userId);

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
            } else {
                handleError(ctx, ar.cause());
            }
        });
    }

    public void updateStatus(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();

        JsonObject request = new JsonObject()
                .put("action", "updateStatus")
                .put("userId", body.getInteger("userId"))
                .put("status", body.getInteger("status"));

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null);
            } else {
                handleError(ctx, ar.cause());
            }
        });
    }

    public void updateRole(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();

        JsonObject request = new JsonObject()
                .put("action", "updateRole")
                .put("userId", body.getInteger("userId"))
                .put("role", body.getInteger("role"));

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null);
            } else {
                handleError(ctx, ar.cause());
            }
        });
    }
}
