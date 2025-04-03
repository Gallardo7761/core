package net.miarma.api.huertos.handlers;

import java.util.stream.Collectors;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.SingleJsonResponse;
import net.miarma.api.huertos.entities.ExpenseEntity;
import net.miarma.api.huertos.services.ExpenseService;

@SuppressWarnings("unused")
public class ExpenseDataHandler {
	private ExpenseService expenseService;
	
	public ExpenseDataHandler(Pool pool) {
		this.expenseService = new ExpenseService(pool);
	}
	
	public void getAll(RoutingContext ctx) {
		expenseService.getAll().onSuccess(expenses -> {
				String result = expenses.stream()
						.map(e -> Constants.GSON.toJson(e, ExpenseEntity.class))
						.collect(Collectors.joining(", ", "[", "]"));
				ctx.response().putHeader("Content-Type", "application/json").end(result);
			})	
			.onFailure(err -> {
				ctx.response().setStatusCode(500).end(Constants.GSON.toJson(SingleJsonResponse.of("Internal server error")));
			});
	}
	
	public void getById(RoutingContext ctx) {
		Integer expenseId = Integer.parseInt(ctx.pathParam("expense_id"));
		expenseService.getById(expenseId).onSuccess(expense -> {
			String result = Constants.GSON.toJson(expense, ExpenseEntity.class);
			ctx.response().putHeader("Content-Type", "application/json").end(result);
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
	
	public void create(RoutingContext ctx) {
		ExpenseEntity expense = Constants.GSON.fromJson(ctx.body().asString(), ExpenseEntity.class);
		expenseService.create(expense).onSuccess(result -> {
			ctx.response().setStatusCode(201).end(result.encode());
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
	
	public void update(RoutingContext ctx) {
		ExpenseEntity expense = Constants.GSON.fromJson(ctx.body().asString(), ExpenseEntity.class);
		expenseService.update(expense).onSuccess(result -> {
			ctx.response().setStatusCode(204).end();
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
	
	public void delete(RoutingContext ctx) {
		Integer expenseId = Integer.parseInt(ctx.pathParam("expense_id"));
		expenseService.delete(expenseId).onSuccess(result -> {
			ctx.response().setStatusCode(204).end();
		}).onFailure(err -> {
			ctx.response().setStatusCode(404).end(Constants.GSON.toJson(SingleJsonResponse.of("Not found")));
		});
	}
}
