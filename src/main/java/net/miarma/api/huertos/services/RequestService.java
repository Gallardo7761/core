package net.miarma.api.huertos.services;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.Constants.HuertosRequestStatus;
import net.miarma.api.common.Constants.HuertosRequestType;
import net.miarma.api.common.Constants.HuertosUserStatus;
import net.miarma.api.common.Constants.HuertosUserType;
import net.miarma.api.common.exceptions.ValidationException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.huertos.dao.RequestDAO;
import net.miarma.api.huertos.entities.RequestEntity;
import net.miarma.api.huertos.entities.ViewRequestsWithPreUsers;
import net.miarma.api.huertos.validators.RequestValidator;
import net.miarma.api.util.MessageUtil;

@SuppressWarnings("unused")
public class RequestService {

	private final RequestDAO requestDAO;
	private final RequestValidator requestValidator;
	private final PreUserService preUserService;
	private final MemberService memberService;

	public RequestService(Pool pool) {
		this.requestDAO = new RequestDAO(pool);
		this.requestValidator = new RequestValidator();
		this.preUserService = new PreUserService(pool);
		this.memberService = new MemberService(pool);
	}

	
	public Future<List<RequestEntity>> getAll() {
		return requestDAO.getAll();
	}
	
	public Future<List<RequestEntity>> getAll(QueryParams params) {
		return requestDAO.getAll(params);
	}

	public Future<RequestEntity> getById(Integer id) {
		return requestDAO.getAll().compose(requests -> {
			RequestEntity request = requests.stream()
				.filter(r -> r.getRequest_id().equals(id))
				.findFirst()
				.orElse(null);

			if (request == null) {
				return Future.failedFuture(MessageUtil.notFound("Request", "with id " + id));
			}

			return Future.succeededFuture(request);
		});
	}
	
	public Future<List<ViewRequestsWithPreUsers>> getRequestsWithPreUsers() {
		return requestDAO.getRequestsWithPreUsers();
	}
	
	public Future<ViewRequestsWithPreUsers> getRequestWithPreUserById(Integer id) {
		return requestDAO.getRequestsWithPreUsers().compose(requests -> {
			ViewRequestsWithPreUsers request = requests.stream()
				.filter(r -> r.getRequest_id().equals(id))
				.findFirst()
				.orElse(null);

			if (request == null) {
				return Future.failedFuture(MessageUtil.notFound("Request", "with id " + id));
			}

			return Future.succeededFuture(request);
		});
	}
	
	public Future<Integer> getRequestCount() {
		return requestDAO.getAll().compose(requests -> {
			return Future.succeededFuture(requests.stream()
				.filter(r -> r.getStatus() == HuertosRequestStatus.PENDING)
				.mapToInt(r -> 1)
				.sum());
		});
	}
	
	public Future<List<RequestEntity>> getMyRequests(String token) {
	    Integer userId = JWTManager.getInstance().getUserId(token);
	    return requestDAO.getAll().compose(requests -> {
	        List<RequestEntity> myRequests = requests.stream()
	            .filter(r -> userId.equals(r.getRequested_by()))
	            .toList();

	        return Future.succeededFuture(myRequests);
	    });
	}
	
	public Future<Boolean> hasCollaboratorRequest(String token) {
    	Integer userId = JWTManager.getInstance().getUserId(token);
    	
    	return getAll().compose(requests -> {
			return Future.succeededFuture(requests.stream()
					.filter(r -> r.getRequested_by().equals(userId))
					.anyMatch(r -> r.getType() == HuertosRequestType.ADD_COLLABORATOR));
		});
    }
    
    public Future<Boolean> hasGreenHouseRequest(String token) {
		Integer userId = JWTManager.getInstance().getUserId(token);
		
		return getAll().compose(requests -> {
			return Future.succeededFuture(requests.stream()
					.filter(r -> r.getRequested_by().equals(userId))
					.anyMatch(r -> r.getType() == HuertosRequestType.ADD_GREENHOUSE));
		});
	}

	public Future<RequestEntity> create(RequestEntity request) {
		return requestValidator.validate(request).compose(validation -> {
			if (!validation.isValid()) {
			    return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
			}
			return requestDAO.insert(request);
		});
	}

	public Future<RequestEntity> update(RequestEntity request) {
		return getById(request.getRequest_id()).compose(existing -> {
			return requestValidator.validate(request).compose(validation -> {
				if (!validation.isValid()) {
				    return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
				}
				return requestDAO.update(request);
			});
		});
	}

	public Future<RequestEntity> delete(Integer id) {
		return getById(id).compose(existing -> {
			return requestDAO.delete(id);
		});
	}
	
	public Future<RequestEntity> acceptRequest(Integer id) {
	    RequestEntity request = new RequestEntity();
	    request.setRequest_id(id);
	    request.setStatus(HuertosRequestStatus.APPROVED);

	    return requestDAO.update(request).compose(updatedRequest -> {
	        return getById(id).compose(fullRequest -> {
	            HuertosRequestType type = fullRequest.getType();

	            switch (type) {
	                case ADD_COLLABORATOR:
	                case REGISTER:
	                    return preUserService.getByRequestId(id).compose(preUser -> {
	                        if (preUser == null) {
	                            return Future.failedFuture("No se encontró preusuario asociado.");
	                        }

	                        return memberService.createFromPreUser(preUser).compose(createdUser ->
	                            preUserService.delete(preUser.getPre_user_id()).map(v -> fullRequest)
	                        );
	                    });
	                    
	                case UNREGISTER:
	                	return memberService.changeMemberStatus(fullRequest.getRequested_by(), HuertosUserStatus.INACTIVE)
	                			.map(v -> fullRequest);
	                	
	                case REMOVE_COLLABORATOR:
	                    return memberService.getById(fullRequest.getRequested_by()).compose(requestingMember -> {
	                    	Integer plotNumber = requestingMember.getPlot_number();

	                        if (plotNumber == null) {
	                            return Future.failedFuture("El miembro solicitante no tiene parcela asignada.");
	                        }

	                        return memberService.getAll().compose(members -> {
	                            return members.stream()
	                                .filter(m -> m.getPlot_number() != null)
	                                .filter(m -> m.getPlot_number().equals(plotNumber))
	                                .filter(m -> m.getType() == HuertosUserType.COLLABORATOR)
	                                .findFirst()
	                                .map(collab -> memberService.changeMemberStatus(collab.getUser_id(), HuertosUserStatus.INACTIVE)
	                                        .map(v -> fullRequest))
	                                .orElse(Future.failedFuture("No se encontró colaborador para esa parcela."));
	                        });
	                    });

	                case ADD_GREENHOUSE:
	                    return memberService.changeMemberType(fullRequest.getRequested_by(), HuertosUserType.WITH_GREENHOUSE)
	                            .map(v -> fullRequest);

	                case REMOVE_GREENHOUSE:
	                    return memberService.changeMemberType(fullRequest.getRequested_by(), HuertosUserType.MEMBER)
	                            .map(v -> fullRequest);

	                default:
	                    return Future.succeededFuture(fullRequest);
	            }
	        });
	    });
	}

	public Future<RequestEntity> rejectRequest(Integer id) {
		RequestEntity request = new RequestEntity();
		request.setRequest_id(id);
		request.setStatus(HuertosRequestStatus.REJECTED);
		return requestDAO.update(request).compose(updatedRequest -> {
			return getById(id);
		});
	}
	
}