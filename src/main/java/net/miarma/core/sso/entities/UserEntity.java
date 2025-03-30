package net.miarma.core.sso.entities;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.Authorization;

public class UserEntity implements User {

	@Override
	public JsonObject attributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public io.vertx.ext.auth.User isAuthorized(Authorization authority, Handler<AsyncResult<Boolean>> resultHandler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonObject principal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAuthProvider(AuthProvider authProvider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public io.vertx.ext.auth.User merge(io.vertx.ext.auth.User other) {
		// TODO Auto-generated method stub
		return null;
	}

}
