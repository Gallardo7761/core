package net.miarma.api.core.handlers;

import java.util.stream.Collectors;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.MainVerticle;
import net.miarma.api.common.SingleJsonResponse;
import net.miarma.api.core.entities.FileEntity;
import net.miarma.api.core.services.FileService;

public class FileDataHandler {
	
	private final FileService fileService;
	
	public FileDataHandler(Pool pool) {
		this.fileService = new FileService(pool);
	}
	
	public void getAll(RoutingContext ctx) {
		fileService.getAll(ar -> {
			if (ar.succeeded()) {
				String result = ar.result().stream()
						.map(u -> MainVerticle.GSON.toJson(u, FileEntity.class))
						.collect(Collectors.joining(", ", "[", "]"));
				ctx.response().putHeader("Content-Type", "application/json").end(result);
			} else {
				ctx.response().setStatusCode(500).end(MainVerticle.GSON.toJson(SingleJsonResponse.of("Internal server error")));
			}
		});
	}
	
	public void getById(RoutingContext ctx) {	
		Integer fileId = Integer.parseInt(ctx.pathParam("file_id"));
		fileService.getById(fileId, ar -> {
			if (ar.succeeded()) {
				String result = MainVerticle.GSON.toJson(ar.result(), FileEntity.class);
				ctx.response().putHeader("Content-Type", "application/json").end(result);
			} else {
				ctx.response().setStatusCode(404).end(MainVerticle.GSON.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}
	
	public void create(RoutingContext ctx) {
		FileEntity file = MainVerticle.GSON.fromJson(ctx.body().asString(), FileEntity.class);
		fileService.create(file, ar -> {
			if (ar.succeeded()) {
				ctx.response().setStatusCode(201).end(ar.result().encode());
			} else {
				ctx.response().setStatusCode(404).end(MainVerticle.GSON.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}
	
	public void update(RoutingContext ctx) {
		FileEntity file = MainVerticle.GSON.fromJson(ctx.body().asString(), FileEntity.class);
		fileService.update(file, ar -> {
			if (ar.succeeded()) {
				ctx.response().setStatusCode(200).end(ar.result().encode());
			} else {
				ctx.response().setStatusCode(404).end(MainVerticle.GSON.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}
	
	public void delete(RoutingContext ctx) {
		Integer fileId = Integer.parseInt(ctx.pathParam("file_id"));
		fileService.delete(fileId, ar -> {
			if (ar.succeeded()) {
				ctx.response().setStatusCode(204).end();
			} else {
				ctx.response().setStatusCode(404).end(MainVerticle.GSON.toJson(SingleJsonResponse.of("Not found")));
			}
		});
	}
}
