package net.miarma.api.microservices.huertosdecine.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.util.DeploymentUtil;

public class CineMainVerticle extends AbstractVerticle {
    private ConfigManager configManager;

    @Override
    public void start(Promise<Void> startPromise) {
        try {
            this.configManager = ConfigManager.getInstance();
            deployVerticles();
            startPromise.complete();
        } catch (Exception e) {
            Constants.LOGGER.error(DeploymentUtil.failMessage(CineMainVerticle.class, e));
            startPromise.fail(e);
        }
    }

    private void deployVerticles() {
        vertx.deployVerticle(new CineDataVerticle(), result -> {
            if (result.succeeded()) {
                Constants.LOGGER.info(DeploymentUtil.successMessage(CineDataVerticle.class));
                Constants.LOGGER.info(DeploymentUtil.apiUrlMessage(
                        configManager.getHost(),
                        configManager.getIntProperty("cine.data.port")
                ));
            } else {
                Constants.LOGGER.error(DeploymentUtil.failMessage(CineDataVerticle.class, result.cause()));
            }
        });

        vertx.deployVerticle(new CineLogicVerticle(), result -> {
            if (result.succeeded()) {
                Constants.LOGGER.info(DeploymentUtil.successMessage(CineLogicVerticle.class));
                Constants.LOGGER.info(DeploymentUtil.apiUrlMessage(
                        configManager.getHost(),
                        configManager.getIntProperty("cine.logic.port")
                ));
            } else {
                Constants.LOGGER.error(DeploymentUtil.failMessage(CineLogicVerticle.class, result.cause()));
            }
        });
    }
}
