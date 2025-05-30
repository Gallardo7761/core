package net.miarma.api.huertos.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.huertos.entities.AnnounceEntity;
import net.miarma.api.huertos.services.AnnounceService;
import net.miarma.api.util.JsonUtil;

@SuppressWarnings("unused")
public class AnnounceDataHandler {
    AnnounceService announceService;

    public AnnounceDataHandler(Pool pool) {
        this.announceService = new AnnounceService(pool);
    }

    public void getAll(RoutingContext ctx) {
        QueryParams params = QueryParams.from(ctx);

        announceService.getAll(params)
            .onSuccess(announces -> JsonUtil.sendJson(ctx, ApiStatus.OK, announces))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void getById(RoutingContext ctx) {
        Integer announceId = Integer.parseInt(ctx.pathParam("announce_id"));

        announceService.getById(announceId)
            .onSuccess(announce -> JsonUtil.sendJson(ctx, ApiStatus.OK, announce))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void create(RoutingContext ctx) {
        AnnounceEntity announce = Constants.GSON.fromJson(ctx.body().asString(), AnnounceEntity.class);

        announceService.create(announce)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void update(RoutingContext ctx) {
        AnnounceEntity announce = Constants.GSON.fromJson(ctx.body().asString(), AnnounceEntity.class);

        announceService.update(announce)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void delete(RoutingContext ctx) {
        Integer announceId = Integer.parseInt(ctx.pathParam("announce_id"));

        announceService.delete(announceId)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }
}