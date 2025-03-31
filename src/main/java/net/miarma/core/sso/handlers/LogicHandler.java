package net.miarma.core.sso.handlers;

import com.google.gson.Gson;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.miarma.core.common.Constants;
import net.miarma.core.common.SingleJsonResponse;
import net.miarma.core.common.security.JWTManager;

public class LogicHandler {

    private final Vertx vertx;
    private final Gson gson = new Gson();
    
    public LogicHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    public void login(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();

        JsonObject request = new JsonObject()
                .put("action", "login")
                .put("email", body.getString("email"))
                .put("password", body.getString("password"))
                .put("keepLoggedIn", body.getBoolean("keepLoggedIn", false));

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                ctx.response().putHeader("Content-Type", "application/json")
                        .end(((JsonObject) ar.result().body())
                        		.put("tokenTime", System.currentTimeMillis())
                        		.encode());
            } else {
                ctx.response().setStatusCode(401).end(
                	gson.toJson(SingleJsonResponse.of("Error requesting the event bus"))
        		);
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
                ctx.response().setStatusCode(201).end();
            } else {
                ctx.response().setStatusCode(400).end(ar.cause().getMessage());
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
                ctx.response().setStatusCode(204).end();
            } else {
                ctx.response().setStatusCode(400).end(ar.cause().getMessage());
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
                if (ar.succeeded() && (Boolean) ar.result().body()) {
                    ctx.response().setStatusCode(200).end(
                    	gson.toJson(SingleJsonResponse.of("Valid token"))
            		);
                } else {
                    ctx.response().setStatusCode(401).end(
                    		gson.toJson(SingleJsonResponse.of("Invalid token"))
            		);
                }
            });
        } else {
            ctx.response().setStatusCode(400).end(
            	gson.toJson(SingleJsonResponse.of("Missing or invalid Authorization header"))
    		);
        }
    }   
    
    public void getInfo(RoutingContext ctx) {
        String authHeader = ctx.request().getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.response().setStatusCode(401).end(gson.toJson(SingleJsonResponse.of("Unauthorized")));
            return;
        }

        String token = authHeader.substring(7);
        int userId = JWTManager.getInstance().getUserId(token);

        if (userId <= 0) {
            ctx.response().setStatusCode(401).end(gson.toJson(SingleJsonResponse.of("Invalid token")));
            return;
        }

        JsonObject request = new JsonObject()
                .put("action", "getInfo")
                .put("userId", userId);

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(((JsonObject) ar.result().body()).encode());
            } else {
                ctx.response()
                        .setStatusCode(404)
                        .end(gson.toJson(SingleJsonResponse.of("User not found")));
            }
        });
    }

    public void exists(RoutingContext ctx) {
        String userId = ctx.pathParam("user_id");

        JsonObject request = new JsonObject()
                .put("action", "userExists")
                .put("userId", Integer.parseInt(userId));

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(((JsonObject) ar.result().body()).encode());
            } else {
                ctx.response()
                        .setStatusCode(500)
                        .end(gson.toJson(SingleJsonResponse.of("Error checking existence")));
            }
        });
    }

    public void getByEmail(RoutingContext ctx) {
        String email = ctx.pathParam("email");

        JsonObject request = new JsonObject()
                .put("action", "getByEmail")
                .put("email", email);

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                ctx.response().putHeader("Content-Type", "application/json")
                        .end(((JsonObject) ar.result().body()).encode());
            } else {
                ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
            }
        });
    }

    public void getByUserName(RoutingContext ctx) {
        String username = ctx.pathParam("user_name");

        JsonObject request = new JsonObject()
                .put("action", "getByUserName")
                .put("userName", username);

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                ctx.response().putHeader("Content-Type", "application/json")
                        .end(((JsonObject) ar.result().body()).encode());
            } else {
                ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
            }
        });
    }

    public void getStatus(RoutingContext ctx) {
        String userId = ctx.pathParam("user_id");

        JsonObject request = new JsonObject()
                .put("action", "getStatus")
                .put("userId", Integer.parseInt(userId));

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                ctx.response().putHeader("Content-Type", "application/json")
                        .end(((JsonObject) ar.result().body()).encode());
            } else {
                ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
            }
        });
    }

    public void getRole(RoutingContext ctx) {
        String userId = ctx.pathParam("user_id");

        JsonObject request = new JsonObject()
                .put("action", "getRole")
                .put("userId", Integer.parseInt(userId));

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                ctx.response().putHeader("Content-Type", "application/json")
                        .end(((JsonObject) ar.result().body()).encode());
            } else {
                ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
            }
        });
    }

    public void getAvatar(RoutingContext ctx) {
        String userId = ctx.pathParam("user_id");

        JsonObject request = new JsonObject()
                .put("action", "getAvatar")
                .put("userId", Integer.parseInt(userId));

        vertx.eventBus().request(Constants.AUTH_EVENT_BUS, request, ar -> {
            if (ar.succeeded()) {
                ctx.response().putHeader("Content-Type", "application/json")
                        .end(((JsonObject) ar.result().body()).encode());
            } else {
                ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
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
				ctx.response().setStatusCode(204).end();
			} else {
				ctx.response().setStatusCode(400).end(ar.cause().getMessage());
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
				ctx.response().setStatusCode(204).end();
			} else {
				ctx.response().setStatusCode(400).end(ar.cause().getMessage());
			}
		});
	}
}
