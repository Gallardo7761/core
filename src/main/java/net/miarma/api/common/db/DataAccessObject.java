package net.miarma.api.common.db;

import java.util.List;

import io.vertx.core.Future;

public interface DataAccessObject<T> {
	public Future<List<T>> getAll();
	public Future<T> insert(T t);
	public Future<T> update(T t);
	public Future<T> delete(Integer id);
}
