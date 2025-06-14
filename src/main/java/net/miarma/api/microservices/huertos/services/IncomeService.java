package net.miarma.api.microservices.huertos.services;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.exceptions.ValidationException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.microservices.huertos.dao.IncomeDAO;
import net.miarma.api.microservices.huertos.entities.IncomeEntity;
import net.miarma.api.microservices.huertos.entities.ViewIncomesWithFullNames;
import net.miarma.api.microservices.huertos.validators.IncomeValidator;

import java.util.List;

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
		return incomeDAO.getById(id);
	}

	public Future<List<IncomeEntity>> getMyIncomes(String token) {
		Integer userId = JWTManager.getInstance().getUserId(token);
		return memberService.getById(userId).compose(memberEntity -> {
			if (memberEntity == null) {
				return Future.failedFuture(new NotFoundException("Member with id " + userId + " not found"));
			}
			return incomeDAO.getUserIncomes(memberEntity.getMember_number());
		});
		
	}
	
	public Future<List<IncomeEntity>> getUserPayments(Integer memberNumber) {
		return incomeDAO.getUserIncomes(memberNumber);
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
				return Future.failedFuture(new NotFoundException("Income in the database"));
			}
			
			return incomeValidator.validate(income).compose(validation -> {
				if (!validation.isValid()) {
				    return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
				}
				return incomeDAO.update(income);
			});
		});
	}

	public Future<Boolean> delete(Integer id) {
		return incomeDAO.delete(id);
	}

	
}
