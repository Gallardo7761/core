package net.miarma.api.huertos.handlers;

import java.util.stream.Collectors;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.SingleJsonResponse;
import net.miarma.api.huertos.entities.AnnounceEntity;
import net.miarma.api.huertos.services.AnnounceService;

@SuppressWarnings("unused")
public class AnnounceDataHandler {
	AnnounceService announceService;
	
	public AnnounceDataHandler(Pool pool) {
		this.announceService = new AnnounceService(pool);
	}
	
	public void getAll(RoutingContext ctx) {
		announceService.getAll().onSuccess(announces -> {
			String result = announces.stream()
					.map(a -> Constants.GSON.toJson(a, AnnounceEntity.class))
					.collect(Collectors.joining(", ", "[", "]"));
			ctx.response().putHeader("Content-Type", "application/json").end(result);
		}).onFailure(err -> {
			ctx.response().setStatusCode(500).end(Constants.GSON.toJson(SingleJsonResponse.of("Internal server error")));
		});
	}
	
	public void getById(RoutingContext ctx) {
		Integer announceId = Integer.parseInt(ctx.pathParam("announce_id"));
		announceService.getById(announceId).onSuccess(announce -> {
			String result = Constants.GSON.toJson(announce, AnnounceEntity.class);
			ctx.response().putHeader("Content-Type", "application/json").end(result);
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
	
	public void create(RoutingContext ctx) {
		AnnounceEntity announce = Constants.GSON.fromJson(ctx.body().asString(), AnnounceEntity.class);
		announceService.create(announce).onSuccess(result -> {
			ctx.response().setStatusCode(201).end(result.encode());
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
	
	public void update(RoutingContext ctx) {
		AnnounceEntity announce = Constants.GSON.fromJson(ctx.body().asString(), AnnounceEntity.class);
		announceService.update(announce).onSuccess(result -> {
			ctx.response().setStatusCode(204).end();
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
	
	public void delete(RoutingContext ctx) {
		Integer announceId = Integer.parseInt(ctx.pathParam("announce_id"));
		announceService.delete(announceId).onSuccess(result -> {
			ctx.response().setStatusCode(204).end();
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
}
