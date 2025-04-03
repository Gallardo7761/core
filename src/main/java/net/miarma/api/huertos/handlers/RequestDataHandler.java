package net.miarma.api.huertos.handlers;

import java.util.stream.Collectors;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.QueryParams;
import net.miarma.api.common.SingleJsonResponse;
import net.miarma.api.huertos.entities.RequestEntity;
import net.miarma.api.huertos.services.RequestService;

@SuppressWarnings("unused")
public class RequestDataHandler {
	private RequestService requestService;
	
	public RequestDataHandler(Pool pool) {
		this.requestService = new RequestService(pool);
	}
	
	public void getAll(RoutingContext ctx) {
		QueryParams params = QueryParams.from(ctx);
		
		requestService.getAll(params).onSuccess(requests -> {
			String result = requests.stream()
					.map(a -> Constants.GSON.toJson(a, RequestEntity.class))
					.collect(Collectors.joining(", ", "[", "]"));
			ctx.response().putHeader("Content-Type", "application/json").end(result);
		}).onFailure(err -> {
			ctx.response().setStatusCode(500).end(Constants.GSON.toJson(SingleJsonResponse.of("Internal server error")));
		});
	}
	
	public void getById(RoutingContext ctx) {
		Integer requestId = Integer.parseInt(ctx.pathParam("request_id"));
		requestService.getById(requestId).onSuccess(request -> {
			String result = Constants.GSON.toJson(request, RequestEntity.class);
			ctx.response().putHeader("Content-Type", "application/json").end(result);
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
	
	public void create(RoutingContext ctx) {
		RequestEntity request = Constants.GSON.fromJson(ctx.body().asString(), RequestEntity.class);
		requestService.create(request).onSuccess(result -> {
			ctx.response().setStatusCode(201).end(result.encode());
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
	
	public void update(RoutingContext ctx) {
		RequestEntity request = Constants.GSON.fromJson(ctx.body().asString(), RequestEntity.class);
		requestService.update(request).onSuccess(result -> {
			ctx.response().setStatusCode(204).end();
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
	
	public void delete(RoutingContext ctx) {
		Integer requestId = Integer.parseInt(ctx.pathParam("request_id"));
		requestService.delete(requestId).onSuccess(result -> {
			ctx.response().setStatusCode(204).end();
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
}
