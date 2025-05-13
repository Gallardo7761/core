package net.miarma.api.common.db;

import java.util.List;

import io.vertx.core.Future;

public interface DataAccessObject<T> {	
	Future<List<T>> getAll();
	Future<T> insert(T t);
	Future<T> update(T t);
	Future<T> delete(Integer id);
}
