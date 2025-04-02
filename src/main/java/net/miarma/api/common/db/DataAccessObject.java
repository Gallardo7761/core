package net.miarma.api.common.db;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface DataAccessObject<T> {
	public void getAll(Handler<AsyncResult<List<T>>> handler);
	public void insert(T user, Handler<AsyncResult<T>> handler);
	public void update(T user, Handler<AsyncResult<T>> handler);
	public void delete(Integer id, Handler<AsyncResult<T>> handler);
}
