package net.miarma.api.microservices.huertos.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertos.entities.PreUserEntity;
import net.miarma.api.microservices.huertos.services.PreUserService;
import net.miarma.api.util.JsonUtil;

@SuppressWarnings("unused")
public class PreUserDataHandler {
    private final PreUserService preUserService;

    public PreUserDataHandler(Pool pool) {
        this.preUserService = new PreUserService(pool);
    }

    public void getAll(RoutingContext ctx) {
        QueryParams params = QueryParams.from(ctx);

        preUserService.getAll(params)
            .onSuccess(preUsers -> JsonUtil.sendJson(ctx, ApiStatus.OK, preUsers))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void getById(RoutingContext ctx) {
        Integer id = Integer.parseInt(ctx.request().getParam("id"));

        preUserService.getById(id)
            .onSuccess(preUser -> JsonUtil.sendJson(ctx, ApiStatus.OK, preUser))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void create(RoutingContext ctx) {
        PreUserEntity preUser = net.miarma.api.common.Constants.GSON.fromJson(ctx.body().asString(), PreUserEntity.class);

        preUserService.create(preUser)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void update(RoutingContext ctx) {
        PreUserEntity preUser = net.miarma.api.common.Constants.GSON.fromJson(ctx.body().asString(), PreUserEntity.class);

        preUserService.update(preUser)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.OK, result))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void delete(RoutingContext ctx) {
        Integer id = Integer.parseInt(ctx.request().getParam("pre_user_id"));

        preUserService.delete(id)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }
}