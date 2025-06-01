package net.miarma.api.microservices.miarmacraft.handlers;

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
	
	public void login(RoutingContext ctx) {
		JsonObject body = ctx.body().asJsonObject();
		JsonObject request = new JsonObject()
			.put("action", "login")
			.put("email", body.getString("email", null))
			.put("userName", body.getString("userName", null))
			.put("password", body.getString("password"))
			.put("keepLoggedIn", body.getBoolean("keepLoggedIn", false));

		vertx.eventBus().request(Constants.MMC_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) {
				JsonObject result = (JsonObject) ar.result().body();
				result.put("tokenTime", System.currentTimeMillis());
				JsonUtil.sendJson(ctx, ApiStatus.OK, result);
			} else {
				EventBusUtil.handleReplyError(ctx, ar.cause(), "The player is inactive or banned");
			}
		});
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
	
	public void getInfo(RoutingContext ctx) {
		String token = ctx.request().getHeader("Authorization").substring("Bearer ".length());
		JsonObject request = new JsonObject().put("action", "getInfo").put("token", token);
		vertx.eventBus().request(Constants.MMC_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else EventBusUtil.handleReplyError(ctx, ar.cause(), "Player not found");
		});
	}
	
	public void playerExists(RoutingContext ctx) {
		Integer playerId = Integer.parseInt(ctx.request().getParam("player_id"));
		JsonObject request = new JsonObject().put("action", "playerExists").put("playerId", playerId);
		vertx.eventBus().request(Constants.MMC_EVENT_BUS, request, ar -> {
			if (ar.succeeded()) JsonUtil.sendJson(ctx, ApiStatus.OK, ar.result().body());
			else EventBusUtil.handleReplyError(ctx, ar.cause(), "Player not found");
		});
	}
	
}
