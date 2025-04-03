package net.miarma.api.huertos.services;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.huertos.dao.IncomeDAO;
import net.miarma.api.huertos.entities.IncomeEntity;

public class IncomeService {

	private final IncomeDAO incomeDAO;

	public IncomeService(Pool pool) {
		this.incomeDAO = new IncomeDAO(pool);
	}

	public Future<List<IncomeEntity>> getAll() {
		return incomeDAO.getAll();
	}

	public Future<IncomeEntity> getById(Integer id) {
		return getAll().compose(incomes -> {
			IncomeEntity income = incomes.stream()
				.filter(i -> i.getIncome_id().equals(id))
				.findFirst()
				.orElse(null);
			return Future.succeededFuture(income);
		});
	}
	
	public Future<List<IncomeEntity>> getUserPayments(Integer memberNumber) {
		return getAll().compose(incomes -> {
			List<IncomeEntity> userPayments = incomes.stream()
				.filter(i -> i.getMember_number().equals(memberNumber))
				.toList();
			return Future.succeededFuture(userPayments);
		});
	}
	
	public Future<Boolean> hasPaid(Integer memberNumber) {
		return getUserPayments(memberNumber).compose(incomes -> {
			boolean hasPaid = incomes.stream()
				.anyMatch(IncomeEntity::isPaid);
			return Future.succeededFuture(hasPaid);
		});
	}

	public Future<IncomeEntity> create(IncomeEntity income) {
		return incomeDAO.insert(income);
	}

	public Future<IncomeEntity> update(IncomeEntity income) {
		return incomeDAO.update(income);
	}

	public Future<IncomeEntity> delete(Integer id) {
		return incomeDAO.delete(id);
	}
}
