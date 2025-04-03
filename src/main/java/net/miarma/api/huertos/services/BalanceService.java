package net.miarma.api.huertos.services;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.huertos.dao.BalanceDAO;
import net.miarma.api.huertos.entities.BalanceEntity;

public class BalanceService {
	private final BalanceDAO balanceDAO;
	
	public BalanceService(Pool pool) {
		this.balanceDAO = new BalanceDAO(pool);
	}
	
	public Future<BalanceEntity> getBalance() {
		return balanceDAO.getAll().compose(balanceList -> {
			if (balanceList.isEmpty()) {
				return Future.failedFuture("No balance found");
			} else {
				return Future.succeededFuture(balanceList.get(0));
			}
		});
	}
	
	public Future<BalanceEntity> update(BalanceEntity balance) {
		return balanceDAO.update(balance).compose(updatedBalance -> {
			if (updatedBalance == null) {
				return Future.failedFuture("Failed to update balance");
			} else {
				return Future.succeededFuture(updatedBalance);
			}
		});
	}
	
	public Future<BalanceEntity> create(BalanceEntity balance) {
		return balanceDAO.insert(balance).compose(createdBalance -> {
			if (createdBalance == null) {
				return Future.failedFuture("Failed to create balance");
			} else {
				return Future.succeededFuture(createdBalance);
			}
		});
	}	
	
}
