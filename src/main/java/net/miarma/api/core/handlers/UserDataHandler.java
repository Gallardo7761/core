package net.miarma.api.core.handlers;

import java.util.stream.Collectors;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.SingleJsonResponse;
import net.miarma.api.core.entities.UserEntity;
import net.miarma.api.core.services.UserService;

@SuppressWarnings("unused")
public class UserDataHandler {

    private final UserService userService;

    public UserDataHandler(Pool pool) {
        this.userService = new UserService(pool);
    }

    public void getAll(RoutingContext ctx) {
        userService.getAll().onSuccess(users -> {
            String result = users.stream()
                .map(u -> Constants.GSON.toJson(u, UserEntity.class))
                .collect(Collectors.joining(", ", "[", "]"));
            ctx.response().putHeader("Content-Type", "application/json").end(result);
        }).onFailure(err -> {
            ctx.response().setStatusCode(500).end(Constants.GSON.toJson(SingleJsonResponse.of("Internal server error")));
        });
    }

    public void getById(RoutingContext ctx) {
        Integer userId = Integer.parseInt(ctx.pathParam("user_id"));
        userService.getById(userId).onSuccess(user -> {
            String result = Constants.GSON.toJson(user, UserEntity.class);
            ctx.response().putHeader("Content-Type", "application/json").end(result);
        }).onFailure(err -> {
            ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
        });
    }

    public void create(RoutingContext ctx) {
        UserEntity user = Constants.GSON.fromJson(ctx.body().asString(), UserEntity.class);
        userService.create(user).onSuccess(result -> {
            ctx.response().setStatusCode(201).end(result.encode());
        }).onFailure(err -> {
            ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
        });
    }

    public void update(RoutingContext ctx) {
        UserEntity user = Constants.GSON.fromJson(ctx.body().asString(), UserEntity.class);
        userService.update(user).onSuccess(result -> {
            ctx.response().setStatusCode(204).end();
        }).onFailure(err -> {
            ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
        });
    }

    public void delete(RoutingContext ctx) {
        Integer userId = Integer.parseInt(ctx.pathParam("user_id"));
        userService.delete(userId).onSuccess(result -> {
            ctx.response().setStatusCode(204).end();
        }).onFailure(err -> {
            ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Bad request")));
        });
    }
} 