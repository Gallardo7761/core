package net.miarma.api.core.handlers;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.LocalDateTimeAdapter;
import net.miarma.api.common.SingleJsonResponse;
import net.miarma.api.core.entities.UserEntity;
import net.miarma.api.core.services.CoreService;

public class DataHandler {

	private final CoreService ssoService;
	private final Gson gson = new GsonBuilder()
			.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
			.create();

	public DataHandler(Pool pool) {
		this.ssoService = new CoreService(pool);
	}

	public void getAll(RoutingContext ctx) {
		ssoService.getAll(ar -> {
			if (ar.succeeded()) {
				String result = ar.result().stream()
						.map(u -> gson.toJson(u, UserEntity.class))
						.collect(Collectors.joining(", ", "[", "]"));
				ctx.response().putHeader("Content-Type", "application/json").end(result);
			} else {
				ctx.response().setStatusCode(500).end(gson.toJson(SingleJsonResponse.of("Internal server error")));
			}
		});
	}

	public void getById(RoutingContext ctx) {	
		Integer userId = Integer.parseInt(ctx.pathParam("user_id"));
		ssoService.getById(userId, ar -> {
			if (ar.succeeded()) {
				String result = gson.toJson(ar.result(), UserEntity.class);
				ctx.response().putHeader("Content-Type", "application/json").end(result);
			} else {
				ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void create(RoutingContext ctx) {
		UserEntity user = gson.fromJson(ctx.body().asString(), UserEntity.class);
		ssoService.create(user, ar -> {
			if (ar.succeeded()) {
				ctx.response().setStatusCode(201).end(ar.result().encode());
			} else {
				ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void update(RoutingContext ctx) {
		UserEntity user = gson.fromJson(ctx.body().asString(), UserEntity.class);
		ssoService.update(user, ar -> {
			if (ar.succeeded()) {
				ctx.response().setStatusCode(204).end();
			} else {
				ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}

	public void delete(RoutingContext ctx) {
		Integer userId = Integer.parseInt(ctx.pathParam("user_id"));
		ssoService.delete(userId, ar -> {
			if (ar.succeeded()) {
				ctx.response().setStatusCode(204).end();
			} else {
				ctx.response().setStatusCode(404).end(gson.toJson(SingleJsonResponse.of("Bad request")));
			}
		});
	}
} 
