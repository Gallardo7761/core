package net.miarma.api.microservices.mpaste.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.mpaste.entities.PasteEntity;
import net.miarma.api.microservices.mpaste.services.PasteService;
import net.miarma.api.util.JsonUtil;

@SuppressWarnings("unused")
public class PasteDataHandler {

    private final PasteService pasteService;

    public PasteDataHandler(Pool pool) {
        this.pasteService = new PasteService(pool);
    }

    public void getAll(RoutingContext ctx) {
        QueryParams params = QueryParams.from(ctx);

        pasteService.getAll(params)
            .onSuccess(pastes -> JsonUtil.sendJson(ctx, ApiStatus.OK, pastes))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void getById(RoutingContext ctx) {
        Long pasteId = Long.parseLong(ctx.pathParam("paste_id"));

        pasteService.getById(pasteId)
            .onSuccess(paste -> JsonUtil.sendJson(ctx, ApiStatus.OK, paste))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void create(RoutingContext ctx) {
        PasteEntity paste = Constants.GSON.fromJson(ctx.body().asString(), PasteEntity.class);

        pasteService.create(paste)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void update(RoutingContext ctx) {
        PasteEntity paste = Constants.GSON.fromJson(ctx.body().asString(), PasteEntity.class);
        pasteService.update(paste);
    }

    public void delete(RoutingContext ctx) {
        Long pasteId = Long.parseLong(ctx.pathParam("paste_id"));

        pasteService.delete(pasteId)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }
}
