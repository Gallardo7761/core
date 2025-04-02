package net.miarma.api.core.handlers;

import java.util.stream.Collectors;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.SingleJsonResponse;
import net.miarma.api.core.entities.FileEntity;
import net.miarma.api.core.services.FileService;

@SuppressWarnings("unused")
public class FileDataHandler {

    private final FileService fileService;

    public FileDataHandler(Pool pool) {
        this.fileService = new FileService(pool);
    }

    public void getAll(RoutingContext ctx) {
        fileService.getAll().onSuccess(files -> {
            String result = files.stream()
                .map(u -> Constants.GSON.toJson(u, FileEntity.class))
                .collect(Collectors.joining(", ", "[", "]"));
            ctx.response().putHeader("Content-Type", "application/json").end(result);
        }).onFailure(err -> {
            ctx.response().setStatusCode(500).end(Constants.GSON.toJson(SingleJsonResponse.of("Internal server error")));
        });
    }

    public void getById(RoutingContext ctx) {
        Integer fileId = Integer.parseInt(ctx.pathParam("file_id"));
        fileService.getById(fileId).onSuccess(file -> {
            String result = Constants.GSON.toJson(file, FileEntity.class);
            ctx.response().putHeader("Content-Type", "application/json").end(result);
        }).onFailure(err -> {
            ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
        });
    }

    public void create(RoutingContext ctx) {
        FileEntity file = Constants.GSON.fromJson(ctx.body().asString(), FileEntity.class);
        fileService.create(file).onSuccess(result -> {
            ctx.response().setStatusCode(201).end(result.encode());
        }).onFailure(err -> {
            ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
        });
    }

    public void update(RoutingContext ctx) {
        FileEntity file = Constants.GSON.fromJson(ctx.body().asString(), FileEntity.class);
        fileService.update(file).onSuccess(result -> {
            ctx.response().setStatusCode(200).end(result.encode());
        }).onFailure(err -> {
            ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
        });
    }

    public void delete(RoutingContext ctx) {
        Integer fileId = Integer.parseInt(ctx.pathParam("file_id"));
        fileService.delete(fileId).onSuccess(result -> {
            ctx.response().setStatusCode(204).end();
        }).onFailure(err -> {
            ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
        });
    }
} 
