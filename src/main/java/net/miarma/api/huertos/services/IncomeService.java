package net.miarma.api.huertos.services;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.exceptions.ValidationException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.huertos.dao.IncomeDAO;
import net.miarma.api.huertos.entities.IncomeEntity;
import net.miarma.api.huertos.entities.ViewIncomesWithFullNames;
import net.miarma.api.huertos.validators.IncomeValidator;
import net.miarma.api.util.MessageUtil;

public class IncomeService {

	private final IncomeDAO incomeDAO;
	private final MemberService memberService;
	private final IncomeValidator incomeValidator;

	public IncomeService(Pool pool) {
		this.incomeDAO = new IncomeDAO(pool);
		this.memberService = new MemberService(pool);
		this.incomeValidator = new IncomeValidator();
	}

	public Future<List<IncomeEntity>> getAll(QueryParams params) {
		return incomeDAO.getAll(params);
	}
	
	public Future<List<ViewIncomesWithFullNames>> getIncomesWithNames(QueryParams params) {
		return incomeDAO.getAllWithNames(params);
	}

	public Future<IncomeEntity> getById(Integer id) {
		return incomeDAO.getAll().compose(incomes -> {
			IncomeEntity income = incomes.stream()
				.filter(i -> i.getIncome_id().equals(id))
				.findFirst()
				.orElse(null);
			if (income == null) {
				return Future.failedFuture(MessageUtil.notFound("Income", "in the database"));
			}
			return Future.succeededFuture(income);
		});
	}

	public Future<List<IncomeEntity>> getMyIncomes(String token) {
		Integer userId = JWTManager.getInstance().getUserId(token);
		return memberService.getById(userId).compose(memberEntity -> {
			if (memberEntity == null) {
				return Future.failedFuture(MessageUtil.notFound("Member", "in the database"));
			}
			return incomeDAO.getAll().compose(incomes -> {
				List<IncomeEntity> myIncomes = incomes.stream()
					.filter(i -> i.getMember_number().equals(memberEntity.getMember_number()))
					.toList();
				return Future.succeededFuture(myIncomes);
			});
		});
		
	}
	
	public Future<List<IncomeEntity>> getUserPayments(Integer memberNumber) {
		return incomeDAO.getAll().compose(incomes -> {
			List<IncomeEntity> userPayments = incomes.stream()
				.filter(i -> i.getMember_number().equals(memberNumber))
				.toList();
			return Future.succeededFuture(userPayments);
		});
	}

	public Future<Boolean> hasPaid(Integer memberNumber) {
		return getUserPayments(memberNumber).compose(incomes -> {
			boolean hasPaid = incomes.stream().anyMatch(IncomeEntity::isPaid);
			return Future.succeededFuture(hasPaid);
		});
	}

	public Future<IncomeEntity> create(IncomeEntity income) {
		return incomeValidator.validate(income).compose(validation -> {
			if (!validation.isValid()) {
			    return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
			}
			return incomeDAO.insert(income);
		});
	}

	public Future<IncomeEntity> update(IncomeEntity income) {
		return getById(income.getIncome_id()).compose(existing -> {
			if (existing == null) {
				return Future.failedFuture(MessageUtil.notFound("Income", "to update"));
			}
			
			return incomeValidator.validate(income).compose(validation -> {
				if (!validation.isValid()) {
				    return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
				}
				return incomeDAO.update(income);
			});
		});
	}

	public Future<IncomeEntity> delete(Integer id) {
		return getById(id).compose(existing -> {
			if (existing == null) {
				return Future.failedFuture(MessageUtil.notFound("Income", "to delete"));
			}
			return incomeDAO.delete(id);
		});
	}

	
}
