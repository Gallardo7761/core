package net.miarma.core.sso.handlers;

import com.google.gson.Gson;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.miarma.core.common.Constants;
import net.miarma.core.common.SingleJsonResponse;

public class AuthHandler {

    private final Vertx vertx;
    private final Gson gson = new Gson();
    
    public AuthHandler(Vertx vertx) {
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
}
