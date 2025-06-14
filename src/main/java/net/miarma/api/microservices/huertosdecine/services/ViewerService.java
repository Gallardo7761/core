package net.miarma.api.microservices.huertosdecine.services;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.exceptions.BadRequestException;
import net.miarma.api.common.exceptions.ForbiddenException;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.common.security.PasswordHasher;
import net.miarma.api.microservices.core.dao.UserDAO;
import net.miarma.api.microservices.core.entities.UserEntity;
import net.miarma.api.microservices.core.services.UserService;
import net.miarma.api.microservices.huertosdecine.dao.UserMetadataDAO;
import net.miarma.api.microservices.huertosdecine.dao.ViewerDAO;
import net.miarma.api.microservices.huertosdecine.entities.UserMetadataEntity;
import net.miarma.api.microservices.huertosdecine.entities.ViewerEntity;
import net.miarma.api.util.UserNameGenerator;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public class ViewerService {
    private final UserDAO userDAO;
    private final UserMetadataDAO userMetadataDAO;
    private final ViewerDAO viewerDAO;
    private final UserService userService;

    public ViewerService(Pool pool) {
        this.userDAO = new UserDAO(pool);
        this.userMetadataDAO = new UserMetadataDAO(pool);
        this.viewerDAO = new ViewerDAO(pool);
        this.userService = new UserService(pool);
    }

    public Future<JsonObject> login(String emailOrUsername, String password, boolean keepLoggedIn) {
        return userService.login(emailOrUsername, password, keepLoggedIn).compose(json -> {
            JsonObject loggedUserJson = json.getJsonObject("loggedUser");
            UserEntity user = Constants.GSON.fromJson(loggedUserJson.encode(), UserEntity.class);

            if (user == null) {
                return Future.failedFuture(new BadRequestException("Invalid credentials"));
            }

            if (user.getGlobal_status() != Constants.CoreUserGlobalStatus.ACTIVE) {
                return Future.failedFuture(new ForbiddenException("User is not active"));
            }

            return userMetadataDAO.getById(user.getUser_id()).compose(metadata -> {
                if (metadata.getStatus() != Constants.CineUserStatus.ACTIVE) {
                    return Future.failedFuture(new ForbiddenException("User is not active"));
                }

                ViewerEntity viewer = new ViewerEntity(user, metadata);

                return Future.succeededFuture(new JsonObject()
                    .put("token", json.getString("token"))
                    .put("loggedUser", new JsonObject(Constants.GSON.toJson(viewer)))
                );
            });
        });
    }

    public Future<List<ViewerEntity>> getAll() {
        return viewerDAO.getAll();
    }

    public Future<List<ViewerEntity>> getAll(QueryParams params) {
        return viewerDAO.getAll(params);
    }

    public Future<ViewerEntity> getById(Integer id) {
        return viewerDAO.getById(id).compose(viewer -> {
            if (viewer == null) {
                return Future.failedFuture(new NotFoundException("Viewer not found in the database"));
            }
            return Future.succeededFuture(viewer);
        });
    }

    public Future<ViewerEntity> create(ViewerEntity viewer) {
        viewer.setPassword(PasswordHasher.hash(viewer.getPassword()));
        if (viewer.getEmail() == null || viewer.getEmail().isBlank()) viewer.setEmail(null);

        String baseName = viewer.getDisplay_name().split(" ")[0].toLowerCase();
        String userName;
        try {
            userName = UserNameGenerator.generateUserName(baseName, viewer.getDisplay_name(), 3);
            viewer.setUser_name(userName);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return userDAO.insert(UserEntity.fromViewerEntity(viewer)).compose(user -> {
           UserMetadataEntity metadata = UserMetadataEntity.fromViewerEntity(viewer);
           metadata.setUser_id(user.getUser_id());
           return userMetadataDAO.upsert(metadata).compose(meta ->
                   userDAO.update(user).map(updatedUser -> new ViewerEntity(updatedUser, meta)));
        });
    }

    public Future<UserMetadataEntity> createMetadata(UserMetadataEntity userMetadata) {
        if (userMetadata.getUser_id() == null) {
            return Future.failedFuture(new BadRequestException("User ID is required"));
        }

        return userMetadataDAO.upsert(userMetadata).compose(Future::succeededFuture);
    }

    public Future<ViewerEntity> update(ViewerEntity viewer) {
        return getById(viewer.getUser_id()).compose(existing -> {
            if (existing == null) {
                return Future.failedFuture(new NotFoundException("Member in the database"));
            }

            if (viewer.getPassword() != null && !viewer.getPassword().isEmpty() &&
                    !viewer.getPassword().equals(existing.getPassword())) {
                viewer.setPassword(PasswordHasher.hash(viewer.getPassword()));
            } else {
                viewer.setPassword(existing.getPassword());
            }

            return userDAO.update(UserEntity.fromViewerEntity(viewer)).compose(updatedUser -> userMetadataDAO.update(UserMetadataEntity.fromViewerEntity(viewer)).map(updatedMeta -> new ViewerEntity(updatedUser, updatedMeta)));
        });
    }

    public Future<ViewerEntity> delete(Integer id) {
        return getById(id).compose(viewer ->
                userDAO.delete(id).compose(deletedUser ->
                        userMetadataDAO.delete(viewer.getUser_id())
                                .map(deletedMetadata -> viewer)
                )
        );
    }

}
