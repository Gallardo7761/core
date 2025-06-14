package net.miarma.api.microservices.huertosdecine.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertosdecine.entities.UserMetadataEntity;
import net.miarma.api.microservices.huertosdecine.entities.ViewerEntity;
import net.miarma.api.microservices.huertosdecine.services.ViewerService;
import net.miarma.api.util.JsonUtil;

public class ViewerDataHandler {
    private final ViewerService viewerService;

    public ViewerDataHandler(Pool pool) {
        this.viewerService = new ViewerService(pool);
    }

    public void getAll(RoutingContext ctx) {
        QueryParams params = QueryParams.from(ctx);

        viewerService.getAll(params)
                .onSuccess(viewers -> JsonUtil.sendJson(ctx, ApiStatus.OK, viewers))
                .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void getById(RoutingContext ctx) {
        Integer viewerId = Integer.parseInt(ctx.pathParam("viewer_id"));

        viewerService.getById(viewerId)
                .onSuccess(viewer -> JsonUtil.sendJson(ctx, ApiStatus.OK, viewer))
                .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void create(RoutingContext ctx) {
        ViewerEntity viewer = Constants.GSON.fromJson(ctx.body().asString(), ViewerEntity.class);

        viewerService.create(viewer)
                .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
                .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void createMetadata(RoutingContext ctx) {
        UserMetadataEntity userMetadata = Constants.GSON.fromJson(ctx.body().asString(), UserMetadataEntity.class);

        viewerService.createMetadata(userMetadata)
                .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
                .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void update(RoutingContext ctx) {
        ViewerEntity viewer = Constants.GSON.fromJson(ctx.body().asString(), ViewerEntity.class);

        viewerService.update(viewer)
                .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
                .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void delete(RoutingContext ctx) {
        Integer viewerId = Integer.parseInt(ctx.pathParam("user_id"));

        viewerService.delete(viewerId)
                .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
                .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

}
