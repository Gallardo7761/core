package net.miarma.core.sso.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.ThreadingModel;
import net.miarma.core.common.ConfigManager;
import net.miarma.core.common.Constants;
import net.miarma.core.util.DeploymentUtil;

public class SSOMainVerticle extends AbstractVerticle {

    private ConfigManager configManager;

    @Override
    public void start(Promise<Void> startPromise) {
        try {
            this.configManager = ConfigManager.getInstance(); 
            deployVerticles();
            startPromise.complete();
        } catch (Exception e) {
            Constants.LOGGER.error(DeploymentUtil.failMessage(SSOMainVerticle.class, e));
            startPromise.fail(e);
        }
    }

    private void deployVerticles() {
        final DeploymentOptions options = new DeploymentOptions()
        		.setThreadingModel(ThreadingModel.WORKER);

        vertx.deployVerticle(new SSODataVerticle(), options, result -> {
            if (result.succeeded()) {
                Constants.LOGGER.info(
                		DeploymentUtil.successMessage(SSODataVerticle.class));
                Constants.LOGGER.info(
                		DeploymentUtil.apiUrlMessage(
                        configManager.getHost(),
                        configManager.getIntProperty("sso.data.port")
                ));
            } else {
                Constants.LOGGER.error(
                		DeploymentUtil.failMessage(SSODataVerticle.class, result.cause()));
            }
        });

        vertx.deployVerticle(new SSOLogicVerticle(), options, result -> {
            if (result.succeeded()) {
                Constants.LOGGER.info(
                		DeploymentUtil.successMessage(SSOLogicVerticle.class));
                Constants.LOGGER.info(
                		DeploymentUtil.apiUrlMessage(
                        configManager.getHost(),
                        configManager.getIntProperty("sso.logic.port")
                ));
            } else {
                Constants.LOGGER.error(
                		DeploymentUtil.failMessage(SSOLogicVerticle.class, result.cause()));
            }
        });
    }
}
