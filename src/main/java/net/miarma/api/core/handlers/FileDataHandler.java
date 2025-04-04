package net.miarma.api.core.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.core.entities.FileEntity;
import net.miarma.api.core.services.FileService;
import net.miarma.api.util.JsonUtil;

@SuppressWarnings("unused")
public class FileDataHandler {

    private final FileService fileService;

    public FileDataHandler(Pool pool) {
        this.fileService = new FileService(pool);
    }

    public void getAll(RoutingContext ctx) {
        QueryParams params = QueryParams.from(ctx);

        fileService.getAll(params)
            .onSuccess(files -> JsonUtil.sendJson(ctx, ApiStatus.OK, files))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void getById(RoutingContext ctx) {
        Integer fileId = Integer.parseInt(ctx.pathParam("file_id"));

        fileService.getById(fileId)
            .onSuccess(file -> JsonUtil.sendJson(ctx, ApiStatus.OK, file))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void create(RoutingContext ctx) {
        FileEntity file = net.miarma.api.common.Constants.GSON.fromJson(ctx.body().asString(), FileEntity.class);

        fileService.create(file)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void update(RoutingContext ctx) {
        FileEntity file = net.miarma.api.common.Constants.GSON.fromJson(ctx.body().asString(), FileEntity.class);

        fileService.update(file)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.OK, result))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void delete(RoutingContext ctx) {
        Integer fileId = Integer.parseInt(ctx.pathParam("file_id"));

        fileService.delete(fileId)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }
}  
