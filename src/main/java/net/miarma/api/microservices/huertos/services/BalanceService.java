package net.miarma.api.microservices.huertos.services;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.exceptions.ValidationException;
import net.miarma.api.microservices.huertos.dao.BalanceDAO;
import net.miarma.api.microservices.huertos.entities.BalanceEntity;
import net.miarma.api.microservices.huertos.entities.ViewBalanceWithTotals;
import net.miarma.api.microservices.huertos.validators.BalanceValidator;

public class BalanceService {
	private final BalanceDAO balanceDAO;
	private final BalanceValidator balanceValidator;

	public BalanceService(Pool pool) {
		this.balanceDAO = new BalanceDAO(pool);
		this.balanceValidator = new BalanceValidator();
	}

	public Future<BalanceEntity> getBalance() {
		return balanceDAO.getAll().compose(balanceList -> {
			if (balanceList.isEmpty()) {
				return Future.failedFuture(new NotFoundException("Balance in the database"));
			}
			return Future.succeededFuture(balanceList.getFirst());
		});
	}
	
	public Future<ViewBalanceWithTotals> getBalanceWithTotals() {
		return balanceDAO.getAllWithTotals().compose(balanceList -> {
			if (balanceList.isEmpty()) {
				return Future.failedFuture(new NotFoundException("Balance in the database"));
			}
			return Future.succeededFuture(balanceList.getFirst());
		});
	}

	public Future<BalanceEntity> update(BalanceEntity balance) {
		return getBalance().compose(existing -> {
			if (existing == null) {
				return Future.failedFuture(new NotFoundException("Balance in the database"));
			}
			
			return balanceValidator.validate(balance).compose(validation -> {
				if (!validation.isValid()) {
				    return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
				}
				return balanceDAO.update(balance);
			});
		});
	}

	public Future<BalanceEntity> create(BalanceEntity balance) {
		return balanceValidator.validate(balance).compose(validation -> {
			if (!validation.isValid()) {
			    return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
			}
			return balanceDAO.insert(balance);
		});
	}
}
