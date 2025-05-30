package net.miarma.api.common.db;

import io.vertx.core.Future;

import java.util.List;

public interface DataAccessObject<T> {	
	Future<List<T>> getAll();
	Future<T> insert(T t);
	Future<T> update(T t);
	Future<T> delete(Integer id);
}
