package net.miarma.api.huertos.services;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.huertos.dao.BalanceDAO;
import net.miarma.api.huertos.entities.BalanceEntity;
import net.miarma.api.util.MessageUtil;

public class BalanceService {
	private final BalanceDAO balanceDAO;

	public BalanceService(Pool pool) {
		this.balanceDAO = new BalanceDAO(pool);
	}

	public Future<BalanceEntity> getBalance() {
		return balanceDAO.getAll().compose(balanceList -> {
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
			return balanceDAO.update(balance);
		});
	}

	public Future<BalanceEntity> create(BalanceEntity balance) {
		return balanceDAO.insert(balance);
	}
}
