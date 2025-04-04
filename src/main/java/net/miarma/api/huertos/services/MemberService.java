package net.miarma.api.huertos.services;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.Constants.HuertosUserRole;
import net.miarma.api.common.Constants.HuertosUserStatus;
import net.miarma.api.common.Constants.HuertosUserType;
import net.miarma.api.common.QueryParams;
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

    
    public Future<List<MemberEntity>> getAll(QueryParams params) {
        return memberDAO.getAll(params);
    }
    
    public Future<MemberEntity> getById(Integer id) {
    	return memberDAO.getAll().map(list -> list.stream()
				.filter(member -> member.getUser_id().equals(id))
				.findFirst()
				.orElse(null));
    }
    
    public Future<MemberEntity> getByMemberNumber(Integer memberNumber) {
		return memberDAO.getAll().map(list -> list.stream()
				.filter(member -> member.getMember_number().equals(memberNumber))
				.findFirst()
				.orElse(null));
	}
    
    public Future<MemberEntity> getByPlotNumber(Integer plotNumber) {
		return memberDAO.getAll().map(list -> list.stream()
				.filter(member -> member.getPlot_number().equals(plotNumber))
				.findFirst()
				.orElse(null));
    			
    }    
    
    public Future<MemberEntity> getByEmail(String email) {
		return memberDAO.getAll().map(list -> list.stream()
				.filter(member -> member.getEmail().equals(email))
				.findFirst()
				.orElse(null));
	}
	
	public Future<MemberEntity> getByDni(String dni) {
		return memberDAO.getAll().map(list -> list.stream()
				.filter(member -> member.getDni().equals(dni))
				.findFirst()
				.orElse(null));
	}
	
	public Future<MemberEntity> getByPhone(String phone) {
		return memberDAO.getAll().map(list -> list.stream()
				.filter(member -> member.getPhone().equals(phone))
				.findFirst()
				.orElse(null));
	}
	
	public Future<List<MemberEntity>> getWaitlist() {
		return memberDAO.getAll().map(list -> list.stream()
				.filter(member -> member.getType().equals(HuertosUserType.WAIT_LIST))
				.toList());
	}
	
	public Future<Integer> getLastMemberNumber() {
		return memberDAO.getAll().map(list -> list.stream()
				.filter(member -> member.getType().equals(HuertosUserType.MEMBER))
				.map(member -> member.getMember_number())
				.max(Integer::compareTo)
				.orElse(0));
	}
	
	public Future<MemberEntity> updateRole(Integer userId, HuertosUserRole role) {
	    return getById(userId).compose(member -> {
	        if (member == null) {
	            return Future.failedFuture(MessageUtil.notFound("Member", "in the database"));
	        }
	        member.setRole(role);
	        return userMetadataDAO.update(UserMetadataEntity.fromMemberEntity(member))
	            .compose(updated -> getById(userId));
	    });
	}

	public Future<MemberEntity> updateStatus(Integer userId, HuertosUserStatus status) {
	    return getById(userId).compose(member -> {
	        if (member == null) {
	            return Future.failedFuture(MessageUtil.notFound("Member", "in the database"));
	        }
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
	    return userDAO.update(UserEntity.fromMemberEntity(member)).compose(user -> {
	        return userMetadataDAO.update(UserMetadataEntity.fromMemberEntity(member))
	            .map(meta -> new MemberEntity(user, meta));
	    });
	}

	public Future<MemberEntity> delete(Integer userId) {
		return getById(userId).compose(member -> {
			if (member == null) {
				return Future.failedFuture(MessageUtil.notFound("Member", "in the database"));
			}
			return userDAO.delete(userId).compose(deletedUser -> {
				return userMetadataDAO.delete(member.getUser_id())
						.map(deletedMetadata -> member);
			});
		});
	}
	
} 
