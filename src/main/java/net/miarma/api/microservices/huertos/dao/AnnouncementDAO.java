package net.miarma.api.microservices.huertos.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertos.entities.AnnouncementEntity;

import java.util.List;
import java.util.Map;

public class AnnouncementDAO implements DataAccessObject<AnnouncementEntity, Integer> {

    private final DatabaseManager db;

    public AnnouncementDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<AnnouncementEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }

    @Override
    public Future<AnnouncementEntity> getById(Integer id) {
        Promise<AnnouncementEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(AnnouncementEntity.class)
                .where(Map.of("announce_id", id))
                .build();

        db.executeOne(query, AnnouncementEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    public Future<List<AnnouncementEntity>> getAll(QueryParams params) {
        Promise<List<AnnouncementEntity>> promise = Promise.promise();
        String query = QueryBuilder
                .select(AnnouncementEntity.class)
                .where(params.getFilters())
                .orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
                .limit(params.getQueryFilters().getLimit())
                .offset(params.getQueryFilters().getOffset())
                .build();

        db.execute(query, AnnouncementEntity.class,
                list -> promise.complete(list.isEmpty() ? List.of() : list),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<AnnouncementEntity> insert(AnnouncementEntity announce) {
        Promise<AnnouncementEntity> promise = Promise.promise();
        String query = QueryBuilder.insert(announce).build();

        db.execute(query, AnnouncementEntity.class,
                list -> promise.complete(list.isEmpty() ? null : list.getFirst()),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<AnnouncementEntity> upsert(AnnouncementEntity announcementEntity, String... conflictKeys) {
        Promise<AnnouncementEntity> promise = Promise.promise();
        String query = QueryBuilder.upsert(announcementEntity, conflictKeys).build();

        db.executeOne(query, AnnouncementEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<AnnouncementEntity> update(AnnouncementEntity announce) {
        Promise<AnnouncementEntity> promise = Promise.promise();
        String query = QueryBuilder.update(announce).build();

        db.executeOne(query, AnnouncementEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> delete(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        AnnouncementEntity announce = new AnnouncementEntity();
        announce.setAnnounce_id(id);

        String query = QueryBuilder.delete(announce).build();

        db.executeOne(query, AnnouncementEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<Boolean> exists(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        String query = QueryBuilder
                .select(AnnouncementEntity.class)
                .where(Map.of("announce_id", id))
                .build();

        db.executeOne(query, AnnouncementEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }
}
