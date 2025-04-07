package net.miarma.api.huertos.routing;

import io.vertx.core.Vertx;
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
import net.miarma.api.huertos.services.MemberService;

public class HuertosDataRouter {
	
	public static void mount(Router router, Vertx vertx, Pool pool) {
		AnnounceDataHandler hAnnounceData = new AnnounceDataHandler(pool);
		BalanceDataHandler hBalanceData = new BalanceDataHandler(pool);
		ExpenseDataHandler hExpenseData = new ExpenseDataHandler(pool);
		IncomeDataHandler hIncomeData = new IncomeDataHandler(pool);
		MemberDataHandler hMemberData = new MemberDataHandler(pool);
		PreUserDataHandler hPreUserData = new PreUserDataHandler(pool);
		RequestDataHandler hRequestData = new RequestDataHandler(pool);
		MemberService memberService = new MemberService(pool);
				
		router.route().handler(BodyHandler.create());
				
		router.get(HuertosEndpoints.ANNOUNCES).handler(AuthGuard.huertosAdmin(memberService)).handler(hAnnounceData::getAll);
		router.get(HuertosEndpoints.ANNOUNCE).handler(AuthGuard.check()).handler(hAnnounceData::getById);
		router.post(HuertosEndpoints.ANNOUNCES).handler(AuthGuard.huertosAdmin(memberService)).handler(hAnnounceData::create);
		router.put(HuertosEndpoints.ANNOUNCE).handler(AuthGuard.huertosAdmin(memberService)).handler(hAnnounceData::update);
		router.delete(HuertosEndpoints.ANNOUNCE).handler(AuthGuard.huertosAdmin(memberService)).handler(hAnnounceData::delete);
		
		router.get(HuertosEndpoints.BALANCE).handler(AuthGuard.huertosAdmin(memberService)).handler(hBalanceData::getBalance);
		router.post(HuertosEndpoints.BALANCE).handler(AuthGuard.huertosAdmin(memberService)).handler(hBalanceData::update);
		router.delete(HuertosEndpoints.BALANCE).handler(AuthGuard.huertosAdmin(memberService)).handler(hBalanceData::create);
		
		router.get(HuertosEndpoints.EXPENSES).handler(AuthGuard.huertosAdmin(memberService)).handler(hExpenseData::getAll);
		router.get(HuertosEndpoints.EXPENSE).handler(AuthGuard.huertosAdmin(memberService)).handler(hExpenseData::getById);
		router.post(HuertosEndpoints.EXPENSES).handler(AuthGuard.huertosAdmin(memberService)).handler(hExpenseData::create);
		router.put(HuertosEndpoints.EXPENSE).handler(AuthGuard.huertosAdmin(memberService)).handler(hExpenseData::update);
		router.delete(HuertosEndpoints.EXPENSE).handler(AuthGuard.huertosAdmin(memberService)).handler(hExpenseData::delete);
		
		router.get(HuertosEndpoints.INCOMES).handler(AuthGuard.huertosAdmin(memberService)).handler(hIncomeData::getAll);
		router.get(HuertosEndpoints.INCOME).handler(AuthGuard.huertosAdmin(memberService)).handler(hIncomeData::getById);
		router.post(HuertosEndpoints.INCOMES).handler(AuthGuard.huertosAdmin(memberService)).handler(hIncomeData::create);
		router.put(HuertosEndpoints.INCOME).handler(AuthGuard.huertosAdmin(memberService)).handler(hIncomeData::update);
		router.delete(HuertosEndpoints.INCOME).handler(AuthGuard.huertosAdmin(memberService)).handler(hIncomeData::delete);
		
		router.get(HuertosEndpoints.MEMBERS).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberData::getAll);
		router.get(HuertosEndpoints.MEMBER).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberData::getById);
		router.post(HuertosEndpoints.MEMBERS).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberData::create);
		router.put(HuertosEndpoints.MEMBER).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberData::update);
		router.delete(HuertosEndpoints.MEMBER).handler(AuthGuard.huertosAdmin(memberService)).handler(hMemberData::delete);
		
		router.get(HuertosEndpoints.PRE_USERS).handler(AuthGuard.huertosAdmin(memberService)).handler(hPreUserData::getAll);
		router.get(HuertosEndpoints.PRE_USER).handler(AuthGuard.huertosAdmin(memberService)).handler(hPreUserData::getById);
		router.post(HuertosEndpoints.PRE_USERS).handler(hPreUserData::create);
		router.put(HuertosEndpoints.PRE_USER).handler(AuthGuard.huertosAdmin(memberService)).handler(hPreUserData::update);
		router.delete(HuertosEndpoints.PRE_USER).handler(AuthGuard.huertosAdmin(memberService)).handler(hPreUserData::delete);
		
		router.get(HuertosEndpoints.REQUESTS).handler(AuthGuard.huertosAdmin(memberService)).handler(hRequestData::getAll);
		router.get(HuertosEndpoints.REQUEST).handler(AuthGuard.huertosAdmin(memberService)).handler(hRequestData::getById);
		router.post(HuertosEndpoints.REQUESTS).handler(hRequestData::create);
		router.put(HuertosEndpoints.REQUEST).handler(AuthGuard.huertosAdmin(memberService)).handler(hRequestData::update);
		router.delete(HuertosEndpoints.REQUEST).handler(AuthGuard.huertosAdmin(memberService)).handler(hRequestData::delete);
	}
}
