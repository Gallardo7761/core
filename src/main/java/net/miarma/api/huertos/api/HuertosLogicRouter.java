package net.miarma.api.huertos.api;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.huertos.handlers.MemberLogicHandler;

public class HuertosLogicRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		MemberLogicHandler hMemberLogic = new MemberLogicHandler(vertx);
		
		router.route().handler(BodyHandler.create());
		
		router.post(HuertosEndpoints.LOGIN).handler(hMemberLogic::login);
	}
}
