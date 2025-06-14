package net.miarma.api.microservices.huertos.services;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.Constants.HuertosUserRole;
import net.miarma.api.common.Constants.HuertosUserStatus;
import net.miarma.api.common.Constants.HuertosUserType;
import net.miarma.api.common.exceptions.BadRequestException;
import net.miarma.api.common.exceptions.ForbiddenException;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.exceptions.ValidationException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.common.security.PasswordHasher;
import net.miarma.api.microservices.core.dao.UserDAO;
import net.miarma.api.microservices.core.entities.UserEntity;
import net.miarma.api.microservices.core.services.UserService;
import net.miarma.api.microservices.huertos.dao.MemberDAO;
import net.miarma.api.microservices.huertos.dao.UserMetadataDAO;
import net.miarma.api.microservices.huertos.entities.MemberEntity;
import net.miarma.api.microservices.huertos.entities.PreUserEntity;
import net.miarma.api.microservices.huertos.entities.UserMetadataEntity;
import net.miarma.api.microservices.huertos.validators.MemberValidator;

import java.util.List;

@SuppressWarnings("unused")
public class MemberService {

    private final UserDAO userDAO;
    private final UserMetadataDAO userMetadataDAO;
    private final MemberDAO memberDAO;
    private final UserService userService;
    private final MemberValidator memberValidator;

    public MemberService(Pool pool) {
        this.userDAO = new UserDAO(pool);
        this.memberDAO = new MemberDAO(pool);
        this.userMetadataDAO = new UserMetadataDAO(pool);
        this.userService = new UserService(pool);
        this.memberValidator = new MemberValidator();
    }
    
    public Future<JsonObject> login(String emailOrUserName, String password, boolean keepLoggedIn) {
        return userService.login(emailOrUserName, password, keepLoggedIn).compose(json -> {
            JsonObject loggedUserJson = json.getJsonObject("loggedUser");
            UserEntity user = Constants.GSON.fromJson(loggedUserJson.encode(), UserEntity.class);
            
            if (user == null) {
				return Future.failedFuture(new BadRequestException("Invalid credentials"));
			}
            
            if (user.getGlobal_status() != Constants.CoreUserGlobalStatus.ACTIVE) {
            	return Future.failedFuture(new ForbiddenException("User is not active"));
            }


            return userMetadataDAO.getById(user.getUser_id()).compose(metadata -> {
                if (metadata.getStatus() != HuertosUserStatus.ACTIVE) {
					return Future.failedFuture(new ForbiddenException("User is not active"));
				}

                MemberEntity member = new MemberEntity(user, metadata);

                return Future.succeededFuture(new JsonObject()
                    .put("token", json.getString("token"))
                    .put("member", new JsonObject(Constants.GSON.toJson(member)))
                );
            });
        });
    }
    
    public Future<List<MemberEntity>> getAll() {
		return memberDAO.getAll().compose(list -> Future.succeededFuture(list.stream()
                .filter(m -> !m.getType().equals(HuertosUserType.DEVELOPER))
                .toList()));
	}

    public Future<List<MemberEntity>> getAll(QueryParams params) {
        return memberDAO.getAll(params).compose(list -> Future.succeededFuture(list.stream()
            .filter(m -> !m.getType().equals(HuertosUserType.DEVELOPER))
            .toList()));
    }

    public Future<MemberEntity> getById(Integer id) {
        return memberDAO.getById(id).compose(member -> {
            if (member == null) {
                return Future.failedFuture(new NotFoundException("Member with id " + id));
            }
            return Future.succeededFuture(member);
        });
    }

    public Future<MemberEntity> getByMemberNumber(Integer memberNumber) {
        return memberDAO.getByMemberNumber(memberNumber).compose(member -> {
            if (member == null) {
                return Future.failedFuture(new NotFoundException("Member with member number " + memberNumber));
            }
            return Future.succeededFuture(member);
        });
    }

    public Future<MemberEntity> getByPlotNumber(Integer plotNumber) {
        return memberDAO.getByPlotNumber(plotNumber).compose(member -> {
            if (member == null) {
                return Future.failedFuture(new NotFoundException("Member with plot number " + plotNumber));
            }
            return Future.succeededFuture(member);
        });
    }

    public Future<MemberEntity> getByEmail(String email) {
        return memberDAO.getByEmail(email).compose(member -> {
            if (member == null) {
                return Future.failedFuture(new NotFoundException("Member with email " + email));
            }
            return Future.succeededFuture(member);
        });
    }

    public Future<MemberEntity> getByDni(String dni) {
        return memberDAO.getByDni(dni).compose(member -> {
            if (member == null) {
                return Future.failedFuture(new NotFoundException("Member with DNI " + dni));
            }
            return Future.succeededFuture(member);
        });
    }

    public Future<MemberEntity> getByPhone(Integer phone) {
        return memberDAO.getByPhone(phone).compose(member -> {
            if (member == null) {
                return Future.failedFuture(new NotFoundException("Member with phone " + phone));
            }
            return Future.succeededFuture(member);
        });
    }

    public Future<List<MemberEntity>> getWaitlist() {
        return memberDAO.getWaitlist().compose(list -> {
            if (list.isEmpty()) {
                return Future.failedFuture(new NotFoundException("No members in the waitlist"));
            }
            return Future.succeededFuture(list);
        });
    }

    public Future<Integer> getLastMemberNumber() {
        return memberDAO.getLastMemberNumber().compose(number -> {
            if (number == null) {
                return Future.failedFuture(new NotFoundException("No members found"));
            }
            return Future.succeededFuture(number);
        });
    }
    
    public Future<Boolean> hasCollaborator(String token) {
        Integer userId = JWTManager.getInstance().getUserId(token);

        return getById(userId).compose(member -> {
            Integer plotNumber = member.getPlot_number();

            if (plotNumber == null || plotNumber == 0) {
                return Future.succeededFuture(false);
            }

            return memberDAO.hasCollaborator(plotNumber).compose(hasCollaborator -> {
                if (!hasCollaborator) {
                    return Future.failedFuture(new NotFoundException("User does not have a collaborator"));
                }
                return Future.succeededFuture(true);
            });
        });
    }

    public Future<MemberEntity> getCollaborator(Integer plotNumber) {
        return memberDAO.getCollaborator(plotNumber).compose(collaborator -> {
            if (collaborator == null) {
                return Future.failedFuture(new NotFoundException("No collaborator found for plot number " + plotNumber));
            }
            return Future.succeededFuture(collaborator);
        });
    }
    
    public Future<Boolean> hasGreenHouse(String token) {
    	Integer userId = JWTManager.getInstance().getUserId(token);

    	return getById(userId).map(user -> user.getType() == HuertosUserType.WITH_GREENHOUSE);
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
        return memberValidator.validate(member).compose(validation -> {
            if (!validation.isValid()) {
                return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
            }

            member.setPassword(PasswordHasher.hash(member.getPassword()));
            if (member.getEmail().isBlank()) member.setEmail(null);

            return userDAO.insert(UserEntity.fromMemberEntity(member)).compose(user -> {
                UserMetadataEntity metadata = UserMetadataEntity.fromMemberEntity(member);
                metadata.setUser_id(user.getUser_id());

                return userMetadataDAO.insert(metadata).compose(meta -> {
                    String baseName = member.getDisplay_name().split(" ")[0].toLowerCase();
                    String userName = baseName + member.getMember_number();

                    user.setUser_name(userName);

                    return userDAO.update(user).map(updatedUser -> new MemberEntity(updatedUser, meta));
                });
            });
        });
    }

    
    public Future<MemberEntity> createFromPreUser(PreUserEntity preUser) {
		MemberEntity memberFromPreUser = MemberEntity.fromPreUser(preUser);
		return memberValidator.validate(memberFromPreUser).compose(validation -> {
			if (!validation.isValid()) {
				return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
			}

			memberFromPreUser.setPassword(PasswordHasher.hash(memberFromPreUser.getPassword()));

			return userDAO.insert(UserEntity.fromMemberEntity(memberFromPreUser)).compose(user -> {
				UserMetadataEntity metadata = UserMetadataEntity.fromMemberEntity(memberFromPreUser);
				metadata.setUser_id(user.getUser_id());

				return userMetadataDAO.insert(metadata)
					.map(meta -> new MemberEntity(user, meta));
			});
		});	
	}

    public Future<MemberEntity> update(MemberEntity member) {
    	return getById(member.getUser_id()).compose(existing -> {
    		if (existing == null) {
    			return Future.failedFuture(new NotFoundException("Member in the database"));
    		}

    		return memberValidator.validate(member).compose(validation -> {
    			if (!validation.isValid()) {
    				return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
    			}

    			if (member.getPassword() != null && !member.getPassword().isEmpty() && 
						!member.getPassword().equals(existing.getPassword())) {
    				member.setPassword(PasswordHasher.hash(member.getPassword()));
    			} else {
    				member.setPassword(existing.getPassword());
    			}

    			return userDAO.update(UserEntity.fromMemberEntity(member)).compose(user -> userMetadataDAO.updateWithNulls(UserMetadataEntity.fromMemberEntity(member))
                        .map(meta -> new MemberEntity(user, meta)));
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
    
    public Future<MemberEntity> changeMemberStatus(Integer userId, HuertosUserStatus status) {
		return getById(userId).compose(member -> {
			member.setStatus(status);
			return userMetadataDAO.update(UserMetadataEntity.fromMemberEntity(member))
				.map(updated -> member);
		});
	}
    
    public Future<MemberEntity> changeMemberType(Integer userId, HuertosUserType type) {
		return getById(userId).compose(member -> {
			member.setType(type);
			return userMetadataDAO.update(UserMetadataEntity.fromMemberEntity(member))
				.map(updated -> member);
			});
    			
    }
}
