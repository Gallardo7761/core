package net.miarma.api.huertos.services;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.exceptions.ValidationException;
import net.miarma.api.huertos.dao.BalanceDAO;
import net.miarma.api.huertos.entities.BalanceEntity;
import net.miarma.api.huertos.entities.ViewBalanceWithTotals;
import net.miarma.api.huertos.validators.BalanceValidator;
import net.miarma.api.util.MessageUtil;

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
				return Future.failedFuture(MessageUtil.notFound("Balance", "in the database"));
			}
			return Future.succeededFuture(balanceList.get(0));
		});
	}
	
	public Future<ViewBalanceWithTotals> getBalanceWithTotals() {
		return balanceDAO.getAllWithTotals().compose(balanceList -> {
			if (balanceList.isEmpty()) {
				return Future.failedFuture(MessageUtil.notFound("Balance", "in the database"));
			}
			return Future.succeededFuture(balanceList.get(0));
		});
	}

	public Future<BalanceEntity> update(BalanceEntity balance) {
		return getBalance().compose(existing -> {
			if (existing == null) {
				return Future.failedFuture(MessageUtil.notFound("Balance", "in the database"));
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
