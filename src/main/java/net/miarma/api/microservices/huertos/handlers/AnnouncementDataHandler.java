package net.miarma.api.microservices.huertos.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertos.entities.AnnouncementEntity;
import net.miarma.api.microservices.huertos.services.AnnouncementService;
import net.miarma.api.util.JsonUtil;

@SuppressWarnings("unused")
public class AnnouncementDataHandler {
    final AnnouncementService announcementService;

    public AnnouncementDataHandler(Pool pool) {
        this.announcementService = new AnnouncementService(pool);
    }

    public void getAll(RoutingContext ctx) {
        QueryParams params = QueryParams.from(ctx);

        announcementService.getAll(params)
            .onSuccess(announces -> JsonUtil.sendJson(ctx, ApiStatus.OK, announces))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void getById(RoutingContext ctx) {
        Integer announcementId = Integer.parseInt(ctx.pathParam("announce_id"));

        announcementService.getById(announcementId)
            .onSuccess(announce -> JsonUtil.sendJson(ctx, ApiStatus.OK, announce))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void create(RoutingContext ctx) {
        AnnouncementEntity announce = Constants.GSON.fromJson(ctx.body().asString(), AnnouncementEntity.class);

        announcementService.create(announce)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void update(RoutingContext ctx) {
        AnnouncementEntity announce = Constants.GSON.fromJson(ctx.body().asString(), AnnouncementEntity.class);

        announcementService.update(announce)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void delete(RoutingContext ctx) {
        Integer announcementId = Integer.parseInt(ctx.pathParam("announce_id"));

        announcementService.delete(announcementId)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }
}