package net.miarma.api.miarmacraft.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.miarmacraft.entities.ModEntity;
import net.miarma.api.miarmacraft.services.ModService;
import net.miarma.api.util.JsonUtil;

public class ModDataHandler {
	ModService modService;
	
	public ModDataHandler(Pool pool) {
		this.modService = new ModService(pool);
	}
	
	public void getAll(RoutingContext ctx) {
		modService.getAll()
			.onSuccess(mods -> JsonUtil.sendJson(ctx, ApiStatus.OK, mods))
			.onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
	}
	
	public void getById(RoutingContext ctx) {
		Integer modId = Integer.parseInt(ctx.pathParam("mod_id"));
		modService.getById(modId)
			.onSuccess(mod -> JsonUtil.sendJson(ctx, ApiStatus.OK, mod))
			.onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
	}
	
	public void create(RoutingContext ctx) {
		ModEntity mod = Constants.GSON.fromJson(ctx.body().asString(), ModEntity.class);
		modService.create(mod)
			.onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
			.onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
	}
	
	public void update(RoutingContext ctx) {
		ModEntity mod = Constants.GSON.fromJson(ctx.body().asString(), ModEntity.class);
		modService.update(mod)
			.onSuccess(_ -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
			.onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
	}
	
	public void delete(RoutingContext ctx) {
		Integer modId = Integer.parseInt(ctx.pathParam("mod_id"));
		modService.delete(modId)
			.onSuccess(_ -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
			.onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
	}
}
