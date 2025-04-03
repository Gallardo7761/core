package net.miarma.api.core.dao;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.QueryFilters;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.core.entities.UserEntity;

public class UserDAO implements DataAccessObject<UserEntity> {

    private final DatabaseManager db;

    public UserDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }
    
    @Override
	public Future<List<UserEntity>> getAll() {
    	return getAll(new QueryFilters());
	}

    public Future<List<UserEntity>> getAll(QueryFilters filters) {
        Promise<List<UserEntity>> promise = Promise.promise();
        String query = QueryBuilder
        		.select(UserEntity.class)
        		.orderBy(filters.getSort(), filters.getOrder())
        		.limit(filters.getLimit())
        		.offset(filters.getOffset())
        		.build();

        db.execute(query, UserEntity.class,
            list -> promise.complete(list.isEmpty() ? List.of() : list),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<UserEntity> insert(UserEntity user) {
        Promise<UserEntity> promise = Promise.promise();
        String query = QueryBuilder.insert(user).build();

        db.execute(query, UserEntity.class,
            list -> promise.complete(list.isEmpty() ? null : list.get(0)),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<UserEntity> update(UserEntity user) {
        Promise<UserEntity> promise = Promise.promise();
        String query = QueryBuilder.update(user).build();

        db.execute(query, UserEntity.class,
            list -> promise.complete(list.isEmpty() ? null : list.get(0)),
            promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<UserEntity> delete(Integer id) {
        Promise<UserEntity> promise = Promise.promise();
        UserEntity user = new UserEntity();
        user.setUser_id(id);

        String query = QueryBuilder.delete(user).build();

        db.execute(query, UserEntity.class,
            list -> promise.complete(list.isEmpty() ? null : list.get(0)),
            promise::fail
        );

        return promise.future();
    }
}
