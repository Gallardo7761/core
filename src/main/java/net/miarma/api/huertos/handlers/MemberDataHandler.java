package net.miarma.api.huertos.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.SingleJsonResponse;
import net.miarma.api.huertos.entities.MemberEntity;
import net.miarma.api.huertos.services.MemberService;

@SuppressWarnings("unused")
public class MemberDataHandler {
	private MemberService memberService;
	
	public MemberDataHandler(Pool pool) {
		this.memberService = new MemberService(pool);
	}
	
	public void getAll(RoutingContext ctx) {
		memberService.getAll().onSuccess(members -> {
			ctx.response()
			    .putHeader("Content-Type", "application/json")
			    .setStatusCode(200).end(Constants.GSON.toJson(members));
		})
		.onFailure(err -> {
			ctx.response()
			    .setStatusCode(500)
			    .end(Constants.GSON.toJson(SingleJsonResponse.of("Internal server error")));
		});
	}
	
	public void getById(RoutingContext ctx) {
		Integer id = Integer.parseInt(ctx.request().getParam("id"));
		memberService.getById(id).onSuccess(member -> {
			if (member == null) {
				ctx.response()
				    .setStatusCode(404)
				    .end(Constants.GSON.toJson(SingleJsonResponse.of("Member not found")));
			} else {
				ctx.response()
				    .putHeader("Content-Type", "application/json")
				    .setStatusCode(200).end(Constants.GSON.toJson(member));
			}
		})
		.onFailure(err -> {
			ctx.response()
			    .setStatusCode(500)
			    .end(Constants.GSON.toJson(SingleJsonResponse.of("Internal server error")));
		});
	}
	
	public void create(RoutingContext ctx) {
		MemberEntity member = Constants.GSON.fromJson(ctx.body().asString(), MemberEntity.class);
		memberService.create(member).onSuccess(result -> {
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
		MemberEntity member = Constants.GSON.fromJson(ctx.body().asString(), MemberEntity.class);
		memberService.update(member).onSuccess(result -> {
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
	
	public void delete(RoutingContext ctx) {
		Integer id = Integer.parseInt(ctx.request().getParam("id"));
		memberService.delete(id).onSuccess(result -> {
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
