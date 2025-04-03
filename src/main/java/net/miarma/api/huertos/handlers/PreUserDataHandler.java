package net.miarma.api.huertos.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.QueryParams;
import net.miarma.api.common.SingleJsonResponse;
import net.miarma.api.huertos.entities.PreUserEntity;
import net.miarma.api.huertos.services.PreUserService;

@SuppressWarnings("unused")
public class PreUserDataHandler {
	private PreUserService preUserService;
	
	public PreUserDataHandler(Pool pool) {
		this.preUserService = new PreUserService(pool);
	}
	
	public void getAll(RoutingContext ctx) {
		QueryParams params = QueryParams.from(ctx);
		
		preUserService.getAll(params).onSuccess(preUsers -> {
			ctx.response()
			    .putHeader("Content-Type", "application/json")
			    .setStatusCode(200).end(Constants.GSON.toJson(preUsers));
		})
		.onFailure(err -> {
			ctx.response()
			    .setStatusCode(500)
			    .end(Constants.GSON.toJson(SingleJsonResponse.of("Internal server error")));
		});
	}
	
	public void getById(RoutingContext ctx) {
		Integer id = Integer.parseInt(ctx.request().getParam("id"));
		preUserService.getById(id).onSuccess(preUser -> {
			ctx.response()
			    .putHeader("Content-Type", "application/json")
			    .setStatusCode(200).end(Constants.GSON.toJson(preUser));
		})
		.onFailure(err -> {
			ctx.response()
			    .setStatusCode(500)
			    .end(Constants.GSON.toJson(SingleJsonResponse.of("Internal server error")));
		});
	}
	
	public void create(RoutingContext ctx) {
		PreUserEntity preUser = Constants.GSON.fromJson(ctx.body().asString(), PreUserEntity.class);
		preUserService.create(preUser).onSuccess(result -> {
			ctx.response()
			    .setStatusCode(201)
			    .putHeader("Content-Type", "application/json")
			    .end(Constants.GSON.toJson(result));
		})
		.onFailure(err -> {
			ctx.response()
			    .setStatusCode(500)
			    .end(Constants.GSON.toJson(SingleJsonResponse.of("Internal server error")));
		});
	}
	
	public void update(RoutingContext ctx) {
		PreUserEntity preUser = Constants.GSON.fromJson(ctx.body().asString(), PreUserEntity.class);
		preUserService.update(preUser).onSuccess(result -> {
			ctx.response()
			    .putHeader("Content-Type", "application/json")
			    .setStatusCode(200).end(Constants.GSON.toJson(result));
		})
		.onFailure(err -> {
			ctx.response()
			    .setStatusCode(500)
			    .end(Constants.GSON.toJson(SingleJsonResponse.of("Internal server error")));
		});
	}
	
	public void delete(RoutingContext ctx) {
		Integer id = Integer.parseInt(ctx.request().getParam("id"));
		preUserService.delete(id).onSuccess(result -> {
			ctx.response()
			    .setStatusCode(204)
			    .end();
		})
		.onFailure(err -> {
			ctx.response()
			    .setStatusCode(500)
			    .end(Constants.GSON.toJson(SingleJsonResponse.of("Internal server error")));
		});
	}
}
