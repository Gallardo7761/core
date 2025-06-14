package net.miarma.api.microservices.huertos.services;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.Constants.HuertosRequestStatus;
import net.miarma.api.common.Constants.HuertosRequestType;
import net.miarma.api.common.Constants.HuertosUserStatus;
import net.miarma.api.common.Constants.HuertosUserType;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.exceptions.ValidationException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.microservices.huertos.dao.RequestDAO;
import net.miarma.api.microservices.huertos.entities.RequestEntity;
import net.miarma.api.microservices.huertos.entities.ViewRequestsWithPreUsers;
import net.miarma.api.microservices.huertos.validators.RequestValidator;

import java.util.List;

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
		return requestDAO.getById(id).compose(request -> {
			if (request == null) {
				return Future.failedFuture(new NotFoundException("Request with id " + id));
			}
			return Future.succeededFuture(request);
		});
	}
	
	public Future<List<ViewRequestsWithPreUsers>> getRequestsWithPreUsers() {
		return requestDAO.getRequestsWithPreUsers();
	}
	
	public Future<ViewRequestsWithPreUsers> getRequestWithPreUserById(Integer id) {
		return requestDAO.getRequestWithPreUserById(id).compose(request -> {
			if (request == null) {
				return Future.failedFuture(new NotFoundException("Request with id " + id));
			}
			return Future.succeededFuture(request);
		});
	}
	
	public Future<Integer> getRequestCount() {
		return requestDAO.getAll().compose(requests -> Future.succeededFuture(requests.stream()
            .filter(r -> r.getStatus() == HuertosRequestStatus.PENDING)
            .mapToInt(r -> 1)
            .sum()));
	}
	
	public Future<List<RequestEntity>> getMyRequests(String token) {
	    Integer userId = JWTManager.getInstance().getUserId(token);
	    return requestDAO.getByUserId(userId).compose(Future::succeededFuture);
	}
	
	public Future<Boolean> hasCollaboratorRequest(String token) {
	    return getMyRequests(token).compose(requests -> {
	        boolean result = requests.stream()
	            .filter(r -> r.getStatus() == HuertosRequestStatus.PENDING)
	            .anyMatch(r -> r.getType() == HuertosRequestType.ADD_COLLABORATOR);
	        return Future.succeededFuture(result);
	    });
	}
    
	public Future<Boolean> hasGreenHouseRequest(String token) {
		return getMyRequests(token).compose(requests -> Future.succeededFuture(requests.stream()
                .filter(r -> r.getStatus() == HuertosRequestStatus.PENDING)
                .anyMatch(r -> r.getType() == HuertosRequestType.ADD_GREENHOUSE)));
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
		return getById(request.getRequest_id()).compose(existing -> requestValidator.validate(request).compose(validation -> {
            if (!validation.isValid()) {
                return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
            }
            return requestDAO.update(request);
        }));
	}

	public Future<Boolean> delete(Integer id) {
		return getById(id).compose(request -> {
			if (request == null) {
				return Future.failedFuture(new NotFoundException("Request with id " + id));
			}
			return requestDAO.delete(id);
		});
	}
	
	public Future<RequestEntity> acceptRequest(Integer id) {
	    RequestEntity request = new RequestEntity();
	    request.setRequest_id(id);
	    request.setStatus(HuertosRequestStatus.APPROVED);

	    return requestDAO.update(request).compose(updatedRequest -> getById(id).compose(fullRequest -> {
            HuertosRequestType type = fullRequest.getType();

            return switch (type) {
                case ADD_COLLABORATOR, REGISTER -> preUserService.getByRequestId(id).compose(preUser -> {
                    if (preUser == null) {
                        return Future.failedFuture("PreUser not found for request id " + id);
                    }

                    return memberService.createFromPreUser(preUser).compose(createdUser ->
                            preUserService.delete(preUser.getPre_user_id()).map(v -> fullRequest)
                    );
                });
                case UNREGISTER ->
                        memberService.changeMemberStatus(fullRequest.getRequested_by(), HuertosUserStatus.INACTIVE)
                                .map(v -> fullRequest);
                case REMOVE_COLLABORATOR ->
                        memberService.getById(fullRequest.getRequested_by()).compose(requestingMember -> {
                            Integer plotNumber = requestingMember.getPlot_number();

                            return memberService.getCollaborator(plotNumber).compose(collaborator -> {
                                if (collaborator == null) {
                                    return Future.failedFuture("No collaborator found for plot number " + plotNumber);
                                }

                                return memberService.changeMemberStatus(collaborator.getUser_id(), HuertosUserStatus.INACTIVE)
                                        .map(v -> fullRequest);
                            });
                        });
                case ADD_GREENHOUSE ->
                        memberService.changeMemberType(fullRequest.getRequested_by(), HuertosUserType.WITH_GREENHOUSE)
                                .map(v -> fullRequest);
                case REMOVE_GREENHOUSE ->
                        memberService.changeMemberType(fullRequest.getRequested_by(), HuertosUserType.MEMBER)
                                .map(v -> fullRequest);
            };
        }));
	}

	public Future<RequestEntity> rejectRequest(Integer id) {
		RequestEntity request = new RequestEntity();
		request.setRequest_id(id);
		request.setStatus(HuertosRequestStatus.REJECTED);
		return requestDAO.update(request);
	}
	
}