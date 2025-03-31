package net.miarma.core.database.entities;

import java.time.Instant;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.Authorization;
import net.miarma.core.common.Table;

@Table("Users")
public class UserEntity implements User {

    private final String userId;
    private final String userName;
    private final String displayName;
    private final String password;
    private final int status;
    private final int role;
    private final Instant createdAt;
    private final Instant updatedAt;

    public UserEntity(String userId, String userName, String displayName, String password, int status, int role, Instant createdAt, Instant updatedAt) {
        this.userId = userId;
        this.userName = userName;
        this.displayName = displayName;
        this.password = password;
        this.status = status;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPassword() {
        return password;
    }

    public int getStatus() {
        return status;
    }

    public int getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public JsonObject attributes() {
        return new JsonObject();
    }

    @Override
    public User isAuthorized(Authorization authority, Handler<AsyncResult<Boolean>> resultHandler) {
        boolean authorized = role > 0;
        resultHandler.handle(Future.succeededFuture(authorized));
        return this;
    }

    @Override
    public JsonObject principal() {
        return new JsonObject()
            .put("sub", userId)
            .put("userName", userName)
            .put("displayName", displayName)
            .put("status", status)
            .put("role", role);
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) {
        // not used
    }

    @Override
    public User merge(User other) {
        JsonObject mergedPrincipal = this.principal().mergeIn(other.principal());
        return new UserEntity(
            mergedPrincipal.getString("sub"),
            mergedPrincipal.getString("userName"),
            mergedPrincipal.getString("displayName"),
            this.password,
            mergedPrincipal.getInteger("status"),
            mergedPrincipal.getInteger("role"),
            this.createdAt,
            this.updatedAt
        );
    }
}
