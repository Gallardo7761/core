package net.miarma.api.miarmacraft.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.miarmacraft.entities.PlayerEntity;
import net.miarma.api.miarmacraft.services.PlayerService;
import net.miarma.api.util.JsonUtil;

public class PlayerDataHandler {
	private PlayerService playerService;
	
	public PlayerDataHandler(Pool pool) {
		this.playerService = new PlayerService(pool);
	}
	
	public void getAll(RoutingContext ctx) {
		playerService.getAll()
			.onSuccess(players -> JsonUtil.sendJson(ctx, ApiStatus.OK, players))
			.onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
	}
	
	public void getById(RoutingContext ctx) {
		Integer playerId = Integer.parseInt(ctx.pathParam("player_id"));
		playerService.getById(playerId)
			.onSuccess(player -> JsonUtil.sendJson(ctx, ApiStatus.OK, player))
			.onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
	}
	
	public void create(RoutingContext ctx) {
		PlayerEntity player = Constants.GSON.fromJson(ctx.body().asString(), PlayerEntity.class);
		playerService.create(player)
			.onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
			.onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
	}
	
	public void update(RoutingContext ctx) {
		PlayerEntity player = Constants.GSON.fromJson(ctx.body().asString(), PlayerEntity.class);
		playerService.update(player)
			.onSuccess(_ -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
			.onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
	}
	
	public void delete(RoutingContext ctx) {
		Integer playerId = Integer.parseInt(ctx.pathParam("player_id"));
		playerService.delete(playerId)
			.onSuccess(_ -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
			.onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
	}
}
