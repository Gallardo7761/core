package net.miarma.api.huertos.services;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.exceptions.ValidationException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.huertos.dao.ExpenseDAO;
import net.miarma.api.huertos.entities.ExpenseEntity;
import net.miarma.api.huertos.validators.ExpenseValidator;

public class ExpenseService {

	private final ExpenseDAO expenseDAO;
	private final ExpenseValidator expenseValidator;

	public ExpenseService(Pool pool) {
		this.expenseDAO = new ExpenseDAO(pool);
		this.expenseValidator = new ExpenseValidator();
	}

	public Future<List<ExpenseEntity>> getAll(QueryParams params) {
		return expenseDAO.getAll(params);
	}

	public Future<ExpenseEntity> getById(Integer id) {
		return expenseDAO.getAll().compose(expenses -> {
			ExpenseEntity expense = expenses.stream()
				.filter(e -> e.getExpense_id().equals(id))
				.findFirst()
				.orElse(null);
			if (expense == null) {
				return Future.failedFuture(new NotFoundException("Expense with id " + id + " not found"));
			}
			return Future.succeededFuture(expense);
		});
	}

	public Future<ExpenseEntity> create(ExpenseEntity expense) {
		return expenseValidator.validate(expense).compose(validation -> {
			if (!validation.isValid()) {
			    return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
			}
			return expenseDAO.insert(expense);
		});
	}

	public Future<ExpenseEntity> update(ExpenseEntity expense) {
		return getById(expense.getExpense_id()).compose(existing -> {
			if (existing == null) {
				return Future.failedFuture(new NotFoundException("Expense not found"));
			}
			
			return expenseValidator.validate(expense).compose(validation -> {
				if (!validation.isValid()) {
				    return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
				}
				return expenseDAO.update(expense);
			});
		});
	}

	public Future<ExpenseEntity> delete(Integer id) {
		return getById(id).compose(existing -> {
			if (existing == null) {
				return Future.failedFuture(new NotFoundException("Expense not found"));
			}
			return expenseDAO.delete(id);
		});
	}
}
