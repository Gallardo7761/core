package net.miarma.api.microservices.core.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.ThreadingModel;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.LogAccumulator;
import net.miarma.api.microservices.huertos.verticles.HuertosLogicVerticle;
import net.miarma.api.util.DeploymentUtil;

public class CoreMainVerticle extends AbstractVerticle {

    private ConfigManager configManager;

    @Override
    public void start(Promise<Void> startPromise) {
        try {
            this.configManager = ConfigManager.getInstance(); 
            deployVerticles();
            startPromise.complete();
        } catch (Exception e) {
            Constants.LOGGER.error(DeploymentUtil.failMessage(CoreMainVerticle.class, e));
            startPromise.fail(e);
        }
    }

    private void deployVerticles() {
        final DeploymentOptions options = new DeploymentOptions()
        		.setThreadingModel(ThreadingModel.WORKER);

        vertx.deployVerticle(new CoreDataVerticle(), options, result -> {
            if (result.succeeded()) {
                String message = String.join("\n\r  ",
                        DeploymentUtil.successMessage(CoreDataVerticle.class),
                        DeploymentUtil.apiUrlMessage(
                                configManager.getHost(),
                                configManager.getIntProperty("sso.data.port")
                        )
                );
                LogAccumulator.add(message);
            } else {
                LogAccumulator.add(DeploymentUtil.failMessage(HuertosLogicVerticle.class, result.cause()));
            }
        });

        vertx.deployVerticle(new CoreLogicVerticle(), options, result -> {
            if (result.succeeded()) {
                String message = String.join("\n\r  ",
                        DeploymentUtil.successMessage(CoreLogicVerticle.class),
                        DeploymentUtil.apiUrlMessage(
                                configManager.getHost(),
                                configManager.getIntProperty("sso.logic.port")
                        )
                );
                LogAccumulator.add(message);
            } else {
                LogAccumulator.add(DeploymentUtil.failMessage(HuertosLogicVerticle.class, result.cause()));
            }
        });
    }
}
