package net.miarma.core.sso.handlers;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.core.sso.entities.UserEntity;
import net.miarma.core.sso.services.AuthService;

public class AuthHandler {

    private final AuthService authService;

    public AuthHandler(Pool pool) {
        this.authService = new AuthService(pool);
    }

    public void login(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        String email = body.getString("email");
        String password = body.getString("password");

        authService.login(email, password, ar -> {
            if (ar.succeeded()) {
                ctx.response().putHeader("Content-Type", "application/json")
                   .end(ar.result().encode());
            } else {
                ctx.response().setStatusCode(401).end(ar.cause().getMessage());
            }
        });
    }

    public void register(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        UserEntity user = new UserEntity();
        user.setUser_name(body.getString("userName"));
        user.setEmail(body.getString("email"));
        user.setDisplay_name(body.getString("displayName"));
        user.setPassword(body.getString("password"));

        authService.register(user, ar -> {
            if (ar.succeeded()) {
                ctx.response().setStatusCode(201).end();
            } else {
                ctx.response().setStatusCode(400).end(ar.cause().getMessage());
            }
        });
    }

    public void changePassword(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        int userId = body.getInteger("userId");
        String newPassword = body.getString("newPassword");

        authService.changePassword(userId, newPassword, ar -> {
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
            boolean valid = authService.validateToken(token);
            if (valid) {
                ctx.response().setStatusCode(200).end("Token válido");
            } else {
                ctx.response().setStatusCode(401).end("Token inválido");
            }
        } else {
            ctx.response().setStatusCode(400).end("Falta el token en el encabezado Authorization");
        }
    }
}