package net.miarma.api.huertos.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.Constants.CoreUserGlobalStatus;
import net.miarma.api.common.Constants.CoreUserRole;
import net.miarma.api.common.Constants.HuertosUserType;
import net.miarma.api.common.QueryFilters;
import net.miarma.api.common.QueryParams;
import net.miarma.api.core.dao.UserDAO;
import net.miarma.api.core.entities.UserEntity;
import net.miarma.api.core.services.UserService;
import net.miarma.api.huertos.dao.UserMetadataDAO;
import net.miarma.api.huertos.entities.MemberEntity;
import net.miarma.api.huertos.entities.UserMetadataEntity;
import net.miarma.api.util.MessageUtil;

@SuppressWarnings("unused")
public class MemberService {

    private final UserDAO userDAO;
    private final UserMetadataDAO userMetadataDAO;
    private final UserService userService;
    
    public MemberService(Pool pool) {
        this.userDAO = new UserDAO(pool);
        this.userMetadataDAO = new UserMetadataDAO(pool);
        this.userService = new UserService(pool);
    }
    
    public Future<JsonObject> login(String emailOrUserName, String password, boolean keepLoggedIn) {
        return userService.login(emailOrUserName, password, keepLoggedIn).compose(json -> {
            JsonObject loggedUserJson = json.getJsonObject("loggedUser");
            UserEntity user = Constants.GSON.fromJson(loggedUserJson.encode(), UserEntity.class);
            
            return userMetadataDAO.getAll().map(metadataList -> {
                UserMetadataEntity metadata = metadataList.stream()
                    .filter(meta -> meta.getUser_id().equals(user.getUser_id()))
                    .findFirst()
                    .orElse(null);


                MemberEntity member = new MemberEntity(user, metadata);
                
                return new JsonObject()
                    .put("token", json.getString("token"))
                    .put("member", new JsonObject(Constants.GSON.toJson(member)));
            });
        });
    }

    public Future<List<MemberEntity>> getAll() {
		return getAll(new QueryParams(Map.of(), new QueryFilters()));
	}
    
    public Future<List<MemberEntity>> getAll(QueryParams params) {
        QueryParams userParams = QueryParams.filterForEntity(params, UserEntity.class, "user");
        QueryParams metadataParams = QueryParams.filterForEntity(params, UserMetadataEntity.class, "metadata");

        if (!metadataParams.getFilters().isEmpty()) {
            return userMetadataDAO.getAll(metadataParams).compose(metadataList -> {
                // Extraemos los user_id de los metadata que coinciden
                List<Integer> userIds = metadataList.stream()
                    .map(UserMetadataEntity::getUser_id)
                    .toList();

                if (userIds.isEmpty()) {
                    // Si no hay coincidencias, no hay que seguir consultando users
                    return Future.succeededFuture(List.of());
                }

                // Creamos un filtro por user_id IN (...)
                String joinedIds = userIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
                userParams.getFilters().put("user_id", "(" + joinedIds + ")");

                return userDAO.getAll(userParams).map(userList ->
                    userList.stream()
                        .map(user -> {
                            UserMetadataEntity metadata = metadataList.stream()
                                .filter(m -> m.getUser_id().equals(user.getUser_id()))
                                .findFirst()
                                .orElse(null);
                            return new MemberEntity(user, metadata);
                        })
                        .toList()
                );
            });
        } else {
            // No hay filtros en metadata, pillamos todos los metadata posibles
            return userDAO.getAll(userParams).compose(userList ->
                userMetadataDAO.getAll().map(metadataList ->
                    userList.stream()
                        .map(user -> {
                            UserMetadataEntity metadata = metadataList.stream()
                                .filter(m -> m.getUser_id().equals(user.getUser_id()))
                                .findFirst()
                                .orElse(null);
                            return new MemberEntity(user, metadata);
                        })
                        .toList()
                )
            );
        }
    }


    
    public Future<MemberEntity> getById(Integer id) {
    	return getAll().map(list -> list.stream()
				.filter(member -> member.getUser().getUser_id().equals(id))
				.findFirst()
				.orElse(null));
    }
    
    public Future<MemberEntity> getByMemberNumber(Integer memberNumber) {
		return getAll().map(list -> list.stream()
				.filter(member -> member.getMetadata().getMember_number().equals(memberNumber))
				.findFirst()
				.orElse(null));
	}
    
    public Future<MemberEntity> getByPlotNumber(Integer plotNumber) {
		return getAll().map(list -> list.stream()
				.filter(member -> member.getMetadata().getPlot_number().equals(plotNumber))
				.findFirst()
				.orElse(null));
    			
    }    
    
    public Future<MemberEntity> getByEmail(String email) {
		return getAll().map(list -> list.stream()
				.filter(member -> member.getUser().getEmail().equals(email))
				.findFirst()
				.orElse(null));
	}
	
	public Future<MemberEntity> getByDni(String dni) {
		return getAll().map(list -> list.stream()
				.filter(member -> member.getMetadata().getDni().equals(dni))
				.findFirst()
				.orElse(null));
	}
	
	public Future<MemberEntity> getByPhone(String phone) {
		return getAll().map(list -> list.stream()
				.filter(member -> member.getMetadata().getPhone().equals(phone))
				.findFirst()
				.orElse(null));
	}
	
	public Future<List<MemberEntity>> getWaitlist() {
		return getAll().map(list -> list.stream()
				.filter(member -> member.getMetadata().getType().equals(HuertosUserType.WAIT_LIST))
				.toList());
	}
	
	public Future<Integer> getLastMemberNumber() {
		return getAll().map(list -> list.stream()
				.filter(member -> member.getMetadata().getType().equals(HuertosUserType.MEMBER))
				.map(member -> member.getMetadata().getMember_number())
				.max(Integer::compareTo)
				.orElse(0));
	}
	
	public Future<MemberEntity> updateRole(Integer userId, CoreUserRole role) {
		return getById(userId).compose(member -> {
			if (member == null) {
				return Future.failedFuture(MessageUtil.notFound("Member", "in the database"));
			}
			member.getUser().setRole(role);
			return userDAO.update(member.getUser()).map(updatedUser -> new MemberEntity(updatedUser, member.getMetadata()));
		});
	}
	
	public Future<MemberEntity> updateStatus(Integer userId, CoreUserGlobalStatus status) {
		return getById(userId).compose(member -> {
			if (member == null) {
				return Future.failedFuture(MessageUtil.notFound("Member", "in the database"));
			}
			member.getUser().setGlobal_status(status);
			return userDAO.update(member.getUser()).map(updatedUser -> new MemberEntity(updatedUser, member.getMetadata()));
		});
	}
	
	public Future<MemberEntity> create(MemberEntity member) {
		return userDAO.insert(member.getUser()).compose(user -> {
			member.getMetadata().setUser_id(user.getUser_id());
			return userMetadataDAO.insert(member.getMetadata())
					.map(metadata -> new MemberEntity(user, metadata));
		});
	}
	
	public Future<MemberEntity> update(MemberEntity member) {
		return userDAO.update(member.getUser()).compose(user -> {
			return userMetadataDAO.update(member.getMetadata())
					.map(metadata -> new MemberEntity(user, metadata));
		});
	}
	
	public Future<MemberEntity> delete(Integer userId) {
		return getById(userId).compose(member -> {
			if (member == null) {
				return Future.failedFuture(MessageUtil.notFound("Member", "in the database"));
			}
			return userDAO.delete(userId).compose(deletedUser -> {
				return userMetadataDAO.delete(member.getMetadata().getUser_id())
						.map(deletedMetadata -> member);
			});
		});
	}
	
} 
