package net.miarma.api.miarmacraft.handlers;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.util.EventBusUtil;
import net.miarma.api.util.JsonUtil;

public class PlayerLogicHandler {
	private final Vertx vertx;
	
	public PlayerLogicHandler(Vertx vertx) {
		this.vertx = vertx;
	}
	
	public void getStatus(RoutingContext ctx) {
		Integer playerId = Integer.parseInt(ctx.request().getParam("player_id"));
		JsonObject request = new JsonObject().put("action", "getStatus").put("playerId", playerId);
		vertx.eventBus().request(Constants.MMC_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else EventBusUtil.handleReplyError(ctx, ar.cause(), "Player not found");
		});
	}
	
	public void updateStatus(RoutingContext ctx) {
		Integer playerId = Integer.parseInt(ctx.request().getParam("player_id"));
		JsonObject body = ctx.body().asJsonObject();
		JsonObject request = new JsonObject().put("action", "updateStatus").put("playerId", playerId)
				.put("status", body.getString("status"));
		vertx.eventBus().request(Constants.MMC_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else EventBusUtil.handleReplyError(ctx, ar.cause(), "Player not found");
		});
	}
	
	public void getRole(RoutingContext ctx) {
		Integer playerId = Integer.parseInt(ctx.request().getParam("player_id"));
		JsonObject request = new JsonObject().put("action", "getRole").put("playerId", playerId);
		vertx.eventBus().request(Constants.MMC_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else EventBusUtil.handleReplyError(ctx, ar.cause(), "Player not found");
		});
	}
	
	public void updateRole(RoutingContext ctx) {
		Integer playerId = Integer.parseInt(ctx.request().getParam("player_id"));
		JsonObject body = ctx.body().asJsonObject();
		JsonObject request = new JsonObject().put("action", "updateRole").put("playerId", playerId)
				.put("role", body.getString("role"));
		vertx.eventBus().request(Constants.MMC_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else EventBusUtil.handleReplyError(ctx, ar.cause(), "Player not found");
		});
	}
	
	public void getAvatar(RoutingContext ctx) {
		Integer playerId = Integer.parseInt(ctx.request().getParam("player_id"));
		JsonObject request = new JsonObject().put("action", "getAvatar").put("playerId", playerId);
		vertx.eventBus().request(Constants.MMC_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else EventBusUtil.handleReplyError(ctx, ar.cause(), "Player not found");
		});
	}
	
	public void updateAvatar(RoutingContext ctx) {
		Integer playerId = Integer.parseInt(ctx.request().getParam("player_id"));
		JsonObject body = ctx.body().asJsonObject();
		JsonObject request = new JsonObject().put("action", "updateAvatar").put("playerId", playerId)
				.put("avatar", body.getString("avatar"));
		vertx.eventBus().request(Constants.MMC_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else EventBusUtil.handleReplyError(ctx, ar.cause(), "Player not found");
		});
	}
}
