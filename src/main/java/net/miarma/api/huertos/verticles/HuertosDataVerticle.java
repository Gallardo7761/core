package net.miarma.api.huertos.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.db.DatabaseProvider;
import net.miarma.api.core.services.FileService;
import net.miarma.api.core.services.UserService;
import net.miarma.api.huertos.api.HuertosDataRouter;
import net.miarma.api.huertos.services.AnnounceService;
import net.miarma.api.huertos.services.BalanceService;
import net.miarma.api.huertos.services.ExpenseService;
import net.miarma.api.huertos.services.IncomeService;
import net.miarma.api.huertos.services.MemberService;
import net.miarma.api.huertos.services.PreUserService;
import net.miarma.api.huertos.services.RequestService;

public class HuertosDataVerticle extends AbstractVerticle {
	
	private ConfigManager configManager;
	private UserService userService;
	private FileService fileService;
	private MemberService memberService;
	private AnnounceService announceService;
	private BalanceService balanceService;
	private ExpenseService expenseService;
	private IncomeService incomeService;
	private RequestService requestService;
	private PreUserService preUserService;
	
	@Override
    public void start(Promise<Void> startPromise) {
        configManager = ConfigManager.getInstance();
        Pool pool = DatabaseProvider.createPool(vertx, configManager);
        
        userService = new UserService(pool);
        fileService = new FileService(pool);
        memberService = new MemberService(pool);
        announceService = new AnnounceService(pool);
        balanceService = new BalanceService(pool);
        expenseService = new ExpenseService(pool);
        incomeService = new IncomeService(pool);
        requestService = new RequestService(pool);
        preUserService = new PreUserService(pool);
        
        Router router = Router.router(vertx);
        HuertosDataRouter.mount(router, vertx, pool);
        registerLogicVerticleConsumer();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(configManager.getIntProperty("huertos.data.port"), res -> {
                if (res.succeeded()) startPromise.complete();
                else startPromise.fail(res.cause());
            });
    }
	
	private void registerLogicVerticleConsumer() {
		vertx.eventBus().consumer(Constants.HUERTOS_EVENT_BUS, message -> {
			JsonObject body = (JsonObject) message.body();
            String action = body.getString("action");
            
            switch (action) {
            	default -> message.fail(400, "Unknown action: " + action);
            }
		});
            
	}

}
