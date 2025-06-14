package net.miarma.api.microservices.huertos.dao;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.db.DataAccessObject;
import net.miarma.api.common.db.DatabaseManager;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryFilters;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.huertos.entities.MemberEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MemberDAO implements DataAccessObject<MemberEntity, Integer> {

    private final DatabaseManager db;

    public MemberDAO(Pool pool) {
        this.db = DatabaseManager.getInstance(pool);
    }

    @Override
    public Future<List<MemberEntity>> getAll() {
        return getAll(new QueryParams(Map.of(), new QueryFilters()));
    }

    @Override
    public Future<MemberEntity> getById(Integer id) {
        Promise<MemberEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(MemberEntity.class)
                .where(Map.of("user_id", id.toString()))
                .build();

        db.executeOne(query, MemberEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    public Future<List<MemberEntity>> getAll(QueryParams params) {
        Promise<List<MemberEntity>> promise = Promise.promise();
        String query = QueryBuilder
                .select(MemberEntity.class)
                .where(params.getFilters())
                .orderBy(params.getQueryFilters().getSort(), params.getQueryFilters().getOrder())
                .limit(params.getQueryFilters().getLimit())
                .offset(params.getQueryFilters().getOffset())
                .build();
        db.execute(query, MemberEntity.class,
                list -> promise.complete(list.isEmpty() ? List.of() : list),
                promise::fail
        );
        return promise.future();
    }

    public Future<MemberEntity> getByMemberNumber(Integer memberNumber) {
        Promise<MemberEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(MemberEntity.class)
                .where(Map.of("member_number", memberNumber.toString()))
                .build();

        db.executeOne(query, MemberEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    public Future<MemberEntity> getByPlotNumber(Integer plotNumber) {
        Promise<MemberEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(MemberEntity.class)
                .where(Map.of("plot_number", plotNumber.toString()))
                .build();

        db.executeOne(query, MemberEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    public Future<MemberEntity> getByEmail(String email) {
        Promise<MemberEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(MemberEntity.class)
                .where(Map.of("email", email))
                .build();

        db.executeOne(query, MemberEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    public Future<MemberEntity> getByDni(String dni) {
        Promise<MemberEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(MemberEntity.class)
                .where(Map.of("dni", dni))
                .build();

        db.executeOne(query, MemberEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    public Future<MemberEntity> getByPhone(Integer phone) {
        Promise<MemberEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(MemberEntity.class)
                .where(Map.of("phone", phone.toString()))
                .build();

        db.executeOne(query, MemberEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    public Future<List<MemberEntity>> getWaitlist() {
        Promise<List<MemberEntity>> promise = Promise.promise();
        String query = QueryBuilder
                .select(MemberEntity.class)
                .where(Map.of("type", "0", "status", String.valueOf(Constants.HuertosUserStatus.ACTIVE.getValue())))
                .build();

        db.execute(query, MemberEntity.class,
                list -> promise.complete(list.isEmpty() ? List.of() : list),
                promise::fail
        );

        return promise.future();
    }

    public Future<Integer> getLastMemberNumber() {
        Promise<Integer> promise = Promise.promise();
        String query = QueryBuilder
                .select(MemberEntity.class, "member_number")
                .orderBy(Optional.of("member_number"), Optional.of("DESC"))
                .limit(Optional.of(1))
                .build();

        db.executeOne(query, MemberEntity.class,
                result -> promise.complete(result != null ? result.getMember_number() : 0),
                promise::fail
        );

        return promise.future();
    }

    public Future<Boolean> hasCollaborator(Integer plotNumber) {
        Promise<Boolean> promise = Promise.promise();
        String query = QueryBuilder
                .select(MemberEntity.class)
                .where(Map.of("plot_number", plotNumber.toString(), "type", String.valueOf(Constants.HuertosUserType.COLLABORATOR.getValue())))
                .build();

        db.executeOne(query, MemberEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }

    public Future<MemberEntity> getCollaborator(Integer plotNumber) {
        Promise<MemberEntity> promise = Promise.promise();
        String query = QueryBuilder
                .select(MemberEntity.class)
                .where(Map.of("plot_number", plotNumber.toString(), "type", String.valueOf(Constants.HuertosUserType.COLLABORATOR.getValue())))
                .build();

        db.executeOne(query, MemberEntity.class,
                promise::complete,
                promise::fail
        );

        return promise.future();
    }

    @Override
    public Future<MemberEntity> insert(MemberEntity user) {
        throw new UnsupportedOperationException("Insert not supported on view-based DAO");
    }

    @Override
    public Future<MemberEntity> upsert(MemberEntity memberEntity, String... conflictKeys) {
        throw new UnsupportedOperationException("Upsert not supported on view-based DAO");
    }

    @Override
    public Future<MemberEntity> update(MemberEntity user) {
        throw new UnsupportedOperationException("Update not supported on view-based DAO");
    }

    @Override
    public Future<Boolean> delete(Integer id) {
        throw new UnsupportedOperationException("Delete not supported on view-based DAO");
    }

    @Override
    public Future<Boolean> exists(Integer id) {
        Promise<Boolean> promise = Promise.promise();
        String query = QueryBuilder
                .select(MemberEntity.class)
                .where(Map.of("user_id", id.toString()))
                .build();

        db.executeOne(query, MemberEntity.class,
                result -> promise.complete(result != null),
                promise::fail
        );

        return promise.future();
    }

}
