package net.miarma.api.huertos.services;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.huertos.dao.RequestDAO;
import net.miarma.api.huertos.entities.RequestEntity;
import net.miarma.api.huertos.entities.ViewRequestsWithPreUsers;
import net.miarma.api.util.MessageUtil;

@SuppressWarnings("unused")
public class RequestService {

	private final RequestDAO requestDAO;

	public RequestService(Pool pool) {
		this.requestDAO = new RequestDAO(pool);
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
			return Future.succeededFuture(requests.size());
		});
	}
	
	public Future<List<RequestEntity>> getMyRequests(String token) {
		Integer userId = JWTManager.getInstance().getUserId(token);
		return requestDAO.getAll().compose(requests -> {
			List<RequestEntity> myRequests = requests.stream()
				.filter(r -> r.getRequested_by().equals(userId))
				.toList();

			if (myRequests.isEmpty()) {
				return Future.failedFuture(MessageUtil.notFound("Request", "for user with id " + userId));
			}

			return Future.succeededFuture(myRequests);
		});
	}

	public Future<RequestEntity> create(RequestEntity request) {
		return requestDAO.insert(request);
	}

	public Future<RequestEntity> update(RequestEntity request) {
		return getById(request.getRequest_id()).compose(existing -> {
			return requestDAO.update(request);
		});
	}

	public Future<RequestEntity> delete(Integer id) {
		return getById(id).compose(existing -> {
			return requestDAO.delete(id);
		});
	}
}
