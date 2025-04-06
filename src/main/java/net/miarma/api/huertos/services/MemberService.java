package net.miarma.api.huertos.services;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.Constants.HuertosUserRole;
import net.miarma.api.common.Constants.HuertosUserStatus;
import net.miarma.api.common.Constants.HuertosUserType;
import net.miarma.api.common.db.QueryBuilder;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.common.security.PasswordHasher;
import net.miarma.api.core.dao.UserDAO;
import net.miarma.api.core.entities.UserEntity;
import net.miarma.api.core.services.UserService;
import net.miarma.api.huertos.dao.MemberDAO;
import net.miarma.api.huertos.dao.UserMetadataDAO;
import net.miarma.api.huertos.entities.MemberEntity;
import net.miarma.api.huertos.entities.UserMetadataEntity;
import net.miarma.api.util.MessageUtil;

@SuppressWarnings("unused")
public class MemberService {

    private final UserDAO userDAO;
    private final UserMetadataDAO userMetadataDAO;
    private final MemberDAO memberDAO;
    private final UserService userService;

    public MemberService(Pool pool) {
        this.userDAO = new UserDAO(pool);
        this.memberDAO = new MemberDAO(pool);
        this.userMetadataDAO = new UserMetadataDAO(pool);
        this.userService = new UserService(pool);
    }

    public Future<JsonObject> login(String emailOrUserName, String password, boolean keepLoggedIn) {
        return userService.login(emailOrUserName, password, keepLoggedIn).compose(json -> {
            JsonObject loggedUserJson = json.getJsonObject("loggedUser");
            UserEntity user = Constants.GSON.fromJson(loggedUserJson.encode(), UserEntity.class);

            return userMetadataDAO.getAll().compose(metadataList -> {
                UserMetadataEntity metadata = metadataList.stream()
                    .filter(meta -> meta.getUser_id().equals(user.getUser_id()))
                    .findFirst()
                    .orElse(null);

                if (metadata == null) {
                    return Future.failedFuture(MessageUtil.notFound("Metadata", "for user"));
                }

                MemberEntity member = new MemberEntity(user, metadata);

                return Future.succeededFuture(new JsonObject()
                    .put("token", json.getString("token"))
                    .put("member", new JsonObject(Constants.GSON.toJson(member)))
                );
            });
        });
    }

    public Future<List<MemberEntity>> getAll(QueryParams params) {
        return memberDAO.getAll(params);
    }

    public Future<MemberEntity> getById(Integer id) {
        return memberDAO.getAll().compose(list -> {
            MemberEntity member = list.stream()
                .filter(m -> m.getUser_id().equals(id))
                .findFirst()
                .orElse(null);
            return member != null ?
                Future.succeededFuture(member) :
                Future.failedFuture(MessageUtil.notFound("Member", "with id " + id));
        });
    }

    public Future<MemberEntity> getByMemberNumber(Integer memberNumber) {
        return memberDAO.getAll().compose(list -> {
            MemberEntity member = list.stream()
                .filter(m -> m.getMember_number().equals(memberNumber))
                .findFirst()
                .orElse(null);
            return member != null ?
                Future.succeededFuture(member) :
                Future.failedFuture(MessageUtil.notFound("Member", "with number " + memberNumber));
        });
    }

    public Future<MemberEntity> getByPlotNumber(Integer plotNumber) {
        return memberDAO.getAll().compose(list -> {
            MemberEntity member = list.stream()
                .filter(m -> m.getPlot_number().equals(plotNumber))
                .findFirst()
                .orElse(null);
            return member != null ?
                Future.succeededFuture(member) :
                Future.failedFuture(MessageUtil.notFound("Member", "with plot number " + plotNumber));
        });
    }

    public Future<MemberEntity> getByEmail(String email) {
        return memberDAO.getAll().compose(list -> {
            MemberEntity member = list.stream()
                .filter(m -> m.getEmail().equals(email))
                .findFirst()
                .orElse(null);
            return member != null ?
                Future.succeededFuture(member) :
                Future.failedFuture(MessageUtil.notFound("Member", "with email " + email));
        });
    }

    public Future<MemberEntity> getByDni(String dni) {
        return memberDAO.getAll().compose(list -> {
            MemberEntity member = list.stream()
                .filter(m -> m.getDni().equals(dni))
                .findFirst()
                .orElse(null);
            return member != null ?
                Future.succeededFuture(member) :
                Future.failedFuture(MessageUtil.notFound("Member", "with DNI " + dni));
        });
    }

    public Future<MemberEntity> getByPhone(String phone) {
        return memberDAO.getAll().compose(list -> {
            MemberEntity member = list.stream()
                .filter(m -> m.getPhone().equals(phone))
                .findFirst()
                .orElse(null);
            return member != null ?
                Future.succeededFuture(member) :
                Future.failedFuture(MessageUtil.notFound("Member", "with phone " + phone));
        });
    }

    public Future<List<MemberEntity>> getWaitlist() {
        return memberDAO.getAll().map(list ->
            list.stream()
                .filter(m -> m.getType().equals(HuertosUserType.WAIT_LIST))
                .filter(m -> m.getStatus().equals(HuertosUserStatus.ACTIVE))
                .toList()
        );
    }

    public Future<Integer> getLastMemberNumber() {
        return memberDAO.getAll().map(list ->
            list.stream()
                .map(MemberEntity::getMember_number)
                .max(Integer::compareTo)
                .orElse(0)
        );
    }

    public Future<MemberEntity> updateRole(Integer userId, HuertosUserRole role) {
        return getById(userId).compose(member -> {
            member.setRole(role);
            return userMetadataDAO.update(UserMetadataEntity.fromMemberEntity(member))
                .compose(updated -> getById(userId));
        });
    }

    public Future<MemberEntity> updateStatus(Integer userId, HuertosUserStatus status) {
        return getById(userId).compose(member -> {
            member.setStatus(status);
            return userMetadataDAO.update(UserMetadataEntity.fromMemberEntity(member))
                .compose(updated -> getById(userId));
        });
    }

    public Future<MemberEntity> create(MemberEntity member) {
        return userDAO.insert(UserEntity.fromMemberEntity(member)).compose(user -> {
            UserMetadataEntity metadata = UserMetadataEntity.fromMemberEntity(member);
            metadata.setUser_id(user.getUser_id());
            return userMetadataDAO.insert(metadata)
                .map(meta -> new MemberEntity(user, meta));
        });
    }

    public Future<MemberEntity> update(MemberEntity member) {
        return getById(member.getUser_id()).compose(existing -> {
        	System.out.println(existing);
            return userDAO.update(UserEntity.fromMemberEntity(member)).compose(user -> {
            	System.out.println(user);
                return userMetadataDAO.update(UserMetadataEntity.fromMemberEntity(member))
	                .map(meta -> {
	                	System.out.println(meta);
	                	return new MemberEntity(user, meta);
	                });
            });
        });
    }

    public Future<MemberEntity> delete(Integer userId) {
        return getById(userId).compose(member -> 
            userDAO.delete(userId).compose(deletedUser -> 
                userMetadataDAO.delete(member.getUser_id())
                    .map(deletedMetadata -> member)
            )
        );
    }
}
