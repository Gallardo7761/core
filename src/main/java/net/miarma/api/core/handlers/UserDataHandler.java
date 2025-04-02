package net.miarma.api.core.handlers;

import java.util.stream.Collectors;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.MainVerticle;
import net.miarma.api.common.SingleJsonResponse;
import net.miarma.api.core.entities.UserEntity;
import net.miarma.api.core.services.UserService;

public class UserDataHandler {

	private final UserService userService;

	public UserDataHandler(Pool pool) {
		this.userService = new UserService(pool);
	}

	public void getAll(RoutingContext ctx) {
		userService.getAll(ar -> {
			if (ar.succeeded()) {
				String result = ar.result().stream()
						.map(u -> MainVerticle.GSON.toJson(u, UserEntity.class))
						.collect(Collectors.joining(", ", "[", "]"));
				ctx.response().putHeader("Content-Type", "application/json").end(result);
			} else {
				ctx.response().setStatusCode(500).end(MainVerticle.GSON.toJson(SingleJsonResponse.of("Internal server error")));
			}
		});
	}

	public void getById(RoutingContext ctx) {	
		Integer userId = Integer.parseInt(ctx.pathParam("user_id"));
		userService.getById(userId, ar -> {
			if (ar.succeeded()) {
				String result = MainVerticle.GSON.toJson(ar.result(), UserEntity.class);
				ctx.response().putHeader("Content-Type", "application/json").end(result);
			} else {
				ctx.response().setStatusCode(404).end(MainVerticle.GSON.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void create(RoutingContext ctx) {
		UserEntity user = MainVerticle.GSON.fromJson(ctx.body().asString(), UserEntity.class);
		userService.create(user, ar -> {
			if (ar.succeeded()) {
				ctx.response().setStatusCode(201).end(ar.result().encode());
			} else {
				ctx.response().setStatusCode(404).end(MainVerticle.GSON.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void update(RoutingContext ctx) {
		UserEntity user = MainVerticle.GSON.fromJson(ctx.body().asString(), UserEntity.class);
		userService.update(user, ar -> {
			if (ar.succeeded()) {
				ctx.response().setStatusCode(204).end();
			} else {
				ctx.response().setStatusCode(404).end(MainVerticle.GSON.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void delete(RoutingContext ctx) {
		Integer userId = Integer.parseInt(ctx.pathParam("user_id"));
		userService.delete(userId, ar -> {
			if (ar.succeeded()) {
				ctx.response().setStatusCode(204).end();
			} else {
				ctx.response().setStatusCode(404).end(MainVerticle.GSON.toJson(SingleJsonResponse.of("Bad request")));
			}
		});
	}
} 
