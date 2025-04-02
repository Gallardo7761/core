package net.miarma.api.huertos.dao;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.IDataAccessObject;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.huertos.entities.RequestEntity;

public class RequestDAO implements IDataAccessObject<RequestEntity> {

    private final DatabaseManager db;

    public RequestDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public void getAll(Handler<AsyncResult<List<RequestEntity>>> handler) {
        String query = QueryBuilder
                .select(RequestEntity.class)
                .build();

        db.execute(query, RequestEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? List.of() : list)),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void insert(RequestEntity request, Handler<AsyncResult<RequestEntity>> handler) {
        String query = QueryBuilder
                .insert(request)
                .build();

        db.execute(query, RequestEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void update(RequestEntity request, Handler<AsyncResult<RequestEntity>> handler) {
        String query = QueryBuilder
                .update(request)
                .build();

        db.execute(query, RequestEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }

    @Override
    public void delete(Integer id, Handler<AsyncResult<RequestEntity>> handler) {
        RequestEntity request = new RequestEntity();
        request.setRequest_id(id);

        String query = QueryBuilder
                .delete(request)
                .build();

        db.execute(query, RequestEntity.class,
            list -> handler.handle(Future.succeededFuture(list.isEmpty() ? null : list.get(0))),
            ex -> handler.handle(Future.failedFuture(ex))
        );
    }
}
