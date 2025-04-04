package net.miarma.api.huertos.api;

import java.util.Set;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.middlewares.AuthGuard;
import net.miarma.api.huertos.handlers.AnnounceDataHandler;
import net.miarma.api.huertos.handlers.BalanceDataHandler;
import net.miarma.api.huertos.handlers.ExpenseDataHandler;
import net.miarma.api.huertos.handlers.IncomeDataHandler;
import net.miarma.api.huertos.handlers.MemberDataHandler;
import net.miarma.api.huertos.handlers.PreUserDataHandler;
import net.miarma.api.huertos.handlers.RequestDataHandler;

public class HuertosDataRouter {
	public static void mount(Router router, Vertx vertx, Pool pool) {
		AnnounceDataHandler hAnnounceData = new AnnounceDataHandler(pool);
		BalanceDataHandler hBalanceData = new BalanceDataHandler(pool);
		ExpenseDataHandler hExpenseData = new ExpenseDataHandler(pool);
		IncomeDataHandler hIncomeData = new IncomeDataHandler(pool);
		MemberDataHandler hMemberData = new MemberDataHandler(pool);
		PreUserDataHandler hPreUserData = new PreUserDataHandler(pool);
		RequestDataHandler hRequestData = new RequestDataHandler(pool);
		
		Set<HttpMethod> allowedMethods = Set.of(HttpMethod.GET, HttpMethod.POST, 
				HttpMethod.PUT, HttpMethod.DELETE,
				HttpMethod.OPTIONS);

		router.route().handler(routingContext -> {
			routingContext.response().putHeader("Access-Control-Allow-Origin", "*");
			routingContext.response().putHeader("Access-Control-Allow-Methods", String.join(",", allowedMethods.stream().map(HttpMethod::name).toArray(String[]::new)));
			routingContext.response().putHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
			routingContext.next();
		});
		
		router.options()
	    	.pathRegex(".*")
			.handler(routingContext -> routingContext.response()
					.putHeader("Content-Type", "application/json")
					.setStatusCode(200).end());
		
		router.route().handler(BodyHandler.create());
		
		router.post("/admin/reset-passwords").handler(hMemberData::resetPasswords);
		
		router.get(HuertosEndpoints.ANNOUNCES).handler(AuthGuard.check()).handler(hAnnounceData::getAll);
		router.get(HuertosEndpoints.ANNOUNCE).handler(AuthGuard.check()).handler(hAnnounceData::getById);
		router.post(HuertosEndpoints.ANNOUNCES).handler(AuthGuard.admin()).handler(hAnnounceData::create);
		router.put(HuertosEndpoints.ANNOUNCE).handler(AuthGuard.admin()).handler(hAnnounceData::update);
		router.delete(HuertosEndpoints.ANNOUNCE).handler(AuthGuard.admin()).handler(hAnnounceData::delete);
		
		router.get(HuertosEndpoints.BALANCE).handler(AuthGuard.admin()).handler(hBalanceData::getBalance);
		router.post(HuertosEndpoints.BALANCE).handler(AuthGuard.admin()).handler(hBalanceData::update);
		router.delete(HuertosEndpoints.BALANCE).handler(AuthGuard.admin()).handler(hBalanceData::create);
		
		router.get(HuertosEndpoints.EXPENSES).handler(AuthGuard.admin()).handler(hExpenseData::getAll);
		router.get(HuertosEndpoints.EXPENSE).handler(AuthGuard.admin()).handler(hExpenseData::getById);
		router.post(HuertosEndpoints.EXPENSES).handler(AuthGuard.admin()).handler(hExpenseData::create);
		router.put(HuertosEndpoints.EXPENSE).handler(AuthGuard.admin()).handler(hExpenseData::update);
		router.delete(HuertosEndpoints.EXPENSE).handler(AuthGuard.admin()).handler(hExpenseData::delete);
		
		router.get(HuertosEndpoints.INCOMES).handler(AuthGuard.admin()).handler(hIncomeData::getAll);
		router.get(HuertosEndpoints.INCOME).handler(AuthGuard.admin()).handler(hIncomeData::getById);
		router.post(HuertosEndpoints.INCOMES).handler(AuthGuard.admin()).handler(hIncomeData::create);
		router.put(HuertosEndpoints.INCOME).handler(AuthGuard.admin()).handler(hIncomeData::update);
		router.delete(HuertosEndpoints.INCOME).handler(AuthGuard.admin()).handler(hIncomeData::delete);
		
		router.get(HuertosEndpoints.MEMBERS).handler(AuthGuard.admin()).handler(hMemberData::getAll);
		router.get(HuertosEndpoints.MEMBER).handler(AuthGuard.admin()).handler(hMemberData::getById);
		router.post(HuertosEndpoints.MEMBERS).handler(AuthGuard.admin()).handler(hMemberData::create);
		router.put(HuertosEndpoints.MEMBER).handler(AuthGuard.admin()).handler(hMemberData::update);
		router.delete(HuertosEndpoints.MEMBER).handler(AuthGuard.admin()).handler(hMemberData::delete);
		
		router.get(HuertosEndpoints.PRE_USERS).handler(AuthGuard.admin()).handler(hPreUserData::getAll);
		router.get(HuertosEndpoints.PRE_USER).handler(AuthGuard.admin()).handler(hPreUserData::getById);
		router.post(HuertosEndpoints.PRE_USERS).handler(hPreUserData::create);
		router.put(HuertosEndpoints.PRE_USER).handler(AuthGuard.admin()).handler(hPreUserData::update);
		router.delete(HuertosEndpoints.PRE_USER).handler(AuthGuard.admin()).handler(hPreUserData::delete);
		
		router.get(HuertosEndpoints.REQUESTS).handler(AuthGuard.admin()).handler(hRequestData::getAll);
		router.get(HuertosEndpoints.REQUEST).handler(AuthGuard.admin()).handler(hRequestData::getById);
		router.post(HuertosEndpoints.REQUESTS).handler(hRequestData::create);
		router.put(HuertosEndpoints.REQUEST).handler(AuthGuard.admin()).handler(hRequestData::update);
		router.delete(HuertosEndpoints.REQUEST).handler(AuthGuard.admin()).handler(hRequestData::delete);
	}
}
