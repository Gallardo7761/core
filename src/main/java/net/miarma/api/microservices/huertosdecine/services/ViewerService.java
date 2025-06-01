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
import net.miarma.api.microservices.huertos.entities.MemberEntity;
import net.miarma.api.microservices.huertosdecine.dao.UserMetadataDAO;
import net.miarma.api.microservices.huertosdecine.dao.ViewerDAO;
import net.miarma.api.microservices.huertosdecine.entities.UserMetadataEntity;
import net.miarma.api.microservices.huertosdecine.entities.ViewerEntity;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.text.View;
import java.util.Arrays;
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

            return userMetadataDAO.getAll().compose(metadataList -> {
                UserMetadataEntity metadata = metadataList.stream()
                    .filter(meta -> meta.getUser_id().equals(user.getUser_id()))
                    .findFirst()
                    .orElse(null);

                if (metadata.getStatus() != Constants.CineUserStatus.ACTIVE) {
                    return Future.failedFuture(new ForbiddenException("User is not active"));
                }

                if (metadata == null) {
                    return Future.failedFuture(new NotFoundException("User metadata not found"));
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
        return viewerDAO.getAll().compose(list -> {
            ViewerEntity viewer = list.stream()
                .filter(v -> v.getUser_id().equals(id))
                .findFirst()
                .orElse(null);
            return viewer != null ?
                Future.succeededFuture(viewer) :
                Future.failedFuture(new NotFoundException("Viewer with id: " + id));
        });
    }

    public Future<ViewerEntity> getByEmail(String email) {
        return viewerDAO.getAll().compose(list -> {
            ViewerEntity viewer = list.stream()
                .filter(v -> v.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
            return viewer != null ?
                Future.succeededFuture(viewer) :
                Future.failedFuture(new NotFoundException("Viewer with email: " + email));
        });
    }

    public Future<ViewerEntity> create(ViewerEntity viewer) {
        viewer.setPassword(PasswordHasher.hash(viewer.getPassword()));
        if (viewer.getEmail().isBlank()) viewer.setEmail(null);

        return userDAO.insert(UserEntity.fromViewerEntity(viewer)).compose(user -> {
           UserMetadataEntity metadata = UserMetadataEntity.fromViewerEntity(viewer);
           metadata.setUser_id(user.getUser_id());

            return userMetadataDAO.insert(metadata).compose(meta -> {
                String baseName = viewer.getDisplay_name().split(" ")[0].toLowerCase();
                char[] hash = {};
                PasswordHasher.hash(viewer.getDisplay_name()).getChars(0, 6, hash, 0);
                String userName = baseName + "-" + Arrays.toString(hash);

                user.setUser_name(userName);

                return userDAO.update(user).map(updatedUser -> new ViewerEntity(updatedUser, meta));
            });
        });
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

            return userDAO.update(UserEntity.fromViewerEntity(viewer)).compose(updatedUser -> {
                return userMetadataDAO.update(UserMetadataEntity.fromViewerEntity(viewer)).map(updatedMeta -> {
                    return new ViewerEntity(updatedUser, updatedMeta);
                });
            });
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
