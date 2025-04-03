package net.miarma.api.huertos.handlers;

import java.util.stream.Collectors;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.SingleJsonResponse;
import net.miarma.api.huertos.entities.IncomeEntity;
import net.miarma.api.huertos.services.IncomeService;

@SuppressWarnings("unused")
public class IncomeDataHandler {
	private IncomeService incomeService;
	
	public IncomeDataHandler(Pool pool) {
		this.incomeService = new IncomeService(pool);
	}
	
	public void getAll(RoutingContext ctx) {
		incomeService.getAll().onSuccess(incomes -> {
				String result = incomes.stream()
						.map(e -> Constants.GSON.toJson(e, IncomeEntity.class))
						.collect(Collectors.joining(", ", "[", "]"));
				ctx.response().putHeader("Content-Type", "application/json").end(result);
			})	
			.onFailure(err -> {
				ctx.response().setStatusCode(500).end(Constants.GSON.toJson(SingleJsonResponse.of("Internal server error")));
			});
	}
	
	public void getById(RoutingContext ctx) {
		Integer incomeId = Integer.parseInt(ctx.pathParam("income_id"));
		incomeService.getById(incomeId).onSuccess(income -> {
			String result = Constants.GSON.toJson(income, IncomeEntity.class);
			ctx.response().putHeader("Content-Type", "application/json").end(result);
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
	
	public void create(RoutingContext ctx) {
		IncomeEntity income = Constants.GSON.fromJson(ctx.body().asString(), IncomeEntity.class);
		incomeService.create(income).onSuccess(result -> {
			ctx.response().setStatusCode(201).end(result.encode());
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
	
	public void update(RoutingContext ctx) {
		IncomeEntity income = Constants.GSON.fromJson(ctx.body().asString(), IncomeEntity.class);
		incomeService.update(income).onSuccess(result -> {
			ctx.response().setStatusCode(204).end();
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
	
	public void delete(RoutingContext ctx) {
		Integer incomeId = Integer.parseInt(ctx.pathParam("income_id"));
		incomeService.delete(incomeId).onSuccess(result -> {
			ctx.response().setStatusCode(204).end();
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
}
