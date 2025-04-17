package net.miarma.api.huertos.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.huertos.entities.RequestEntity;
import net.miarma.api.huertos.services.RequestService;
import net.miarma.api.util.JsonUtil;

@SuppressWarnings("unused")
public class RequestDataHandler {
    private final RequestService requestService;

    public RequestDataHandler(Pool pool) {
        this.requestService = new RequestService(pool);
    }

    public void getAll(RoutingContext ctx) {
        QueryParams params = QueryParams.from(ctx);

        requestService.getAll(params)
            .onSuccess(requests -> JsonUtil.sendJson(ctx, ApiStatus.OK, requests))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void getById(RoutingContext ctx) {
        Integer requestId = Integer.parseInt(ctx.pathParam("request_id"));

        requestService.getById(requestId)
            .onSuccess(request -> JsonUtil.sendJson(ctx, ApiStatus.OK, request))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void create(RoutingContext ctx) {
        RequestEntity request = net.miarma.api.common.Constants.GSON.fromJson(ctx.body().asString(), RequestEntity.class);
        
        requestService.create(request)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void update(RoutingContext ctx) {
        RequestEntity request = net.miarma.api.common.Constants.GSON.fromJson(ctx.body().asString(), RequestEntity.class);

        requestService.update(request)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void delete(RoutingContext ctx) {
        Integer requestId = Integer.parseInt(ctx.pathParam("request_id"));

        requestService.delete(requestId)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }
}