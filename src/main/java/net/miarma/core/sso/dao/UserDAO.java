package net.miarma.core.sso.dao;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import net.miarma.core.common.db.DatabaseManager;
import net.miarma.core.common.db.QueryBuilder;
import net.miarma.core.sso.entities.UserEntity;

public class UserDAO {

    private final DatabaseManager db;

    public UserDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    public void findByEmail(String email, Handler<AsyncResult<UserEntity>> handler) {
        UserEntity filter = new UserEntity();
        filter.setEmail(email);
        String query = QueryBuilder.select(filter).build();

        db.execute(query, UserEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @SuppressWarnings("unused")
	public void insert(UserEntity user, Handler<AsyncResult<Void>> handler) {
        String query = QueryBuilder.insert(user).build();
        db.getPool().query(query).execute()
            .onSuccess(_rows -> handler.handle(Future.succeededFuture()))
            .onFailure(err -> handler.handle(Future.failedFuture(err)));
    }
    
    @SuppressWarnings("unused")
    public void updatePassword(int userId, String hashedPassword, Handler<AsyncResult<Void>> handler) {
        String query = "UPDATE users SET password = '" + hashedPassword + "' WHERE user_id = " + userId + ";";
        db.getPool().query(query).execute()
            .onSuccess(_res -> handler.handle(Future.succeededFuture()))
            .onFailure(err -> handler.handle(Future.failedFuture(err)));
    }
}