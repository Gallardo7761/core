package net.miarma.api;

import io.vertx.core.*;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.LogAccumulator;
import net.miarma.api.common.security.SecretManager;
import net.miarma.api.common.vertx.VertxJacksonConfig;
import net.miarma.api.microservices.core.verticles.CoreMainVerticle;
import net.miarma.api.microservices.huertos.verticles.HuertosLogicVerticle;
import net.miarma.api.microservices.huertos.verticles.HuertosMainVerticle;
import net.miarma.api.microservices.huertosdecine.verticles.CineMainVerticle;
import net.miarma.api.microservices.miarmacraft.verticles.MMCMainVerticle;
import net.miarma.api.microservices.mpaste.verticles.MPasteMainVerticle;
import net.miarma.api.util.DeploymentUtil;
import net.miarma.api.util.MessageUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class MainVerticle extends AbstractVerticle {
	private ConfigManager configManager;
	
	public static void main(String[] args) {
		Launcher.executeCommand("run", MainVerticle.class.getName());
	}
	
	private void init() {	
		this.configManager = ConfigManager.getInstance();
	    initializeDirectories();
	    copyDefaultConfig();
	    this.configManager.loadConfig();
	    SecretManager.getOrCreateSecret();
        VertxJacksonConfig.configure();
    }
	
	@Override
	public void start(Promise<Void> startPromise) {
		try {
			init();
			deploy()
				.onSuccess(v -> {
					vertx.setTimer(300, id -> {
						LogAccumulator.flushToLogger(Constants.LOGGER);
						startPromise.complete();
					});
				})
				.onFailure(startPromise::fail);
		} catch (Exception e) {
			startPromise.fail(e);
		}
	}

	@Override
	public void stop(Promise<Void> stopPromise) {
		vertx.deploymentIDs().forEach(id -> vertx.undeploy(id));
		stopPromise.complete();
	}

	private Future<Void> deploy() {
		Promise<Void> promise = Promise.promise();

		Future<String> core = vertx.deployVerticle(new CoreMainVerticle())
				.onSuccess(id -> LogAccumulator.add(DeploymentUtil.successMessage(CoreMainVerticle.class)))
				.onFailure(err -> LogAccumulator.add(DeploymentUtil.failMessage(CoreMainVerticle.class, err)));

		Future<String> huertos = vertx.deployVerticle(new HuertosMainVerticle())
				.onSuccess(id -> LogAccumulator.add(DeploymentUtil.successMessage(HuertosMainVerticle.class)))
				.onFailure(err -> LogAccumulator.add(DeploymentUtil.failMessage(HuertosMainVerticle.class, err)));

		Future<String> mmc = vertx.deployVerticle(new MMCMainVerticle())
				.onSuccess(id -> LogAccumulator.add(DeploymentUtil.successMessage(MMCMainVerticle.class)))
				.onFailure(err -> LogAccumulator.add(DeploymentUtil.failMessage(MMCMainVerticle.class, err)));

		Future<String> cine = vertx.deployVerticle(new CineMainVerticle())
				.onSuccess(id -> LogAccumulator.add(DeploymentUtil.successMessage(CineMainVerticle.class)))
				.onFailure(err -> LogAccumulator.add(DeploymentUtil.failMessage(CineMainVerticle.class, err)));

		Future<String> mpaste = vertx.deployVerticle(new MPasteMainVerticle())
				.onSuccess(id -> LogAccumulator.add(DeploymentUtil.successMessage(MPasteMainVerticle.class)))
				.onFailure(err -> LogAccumulator.add(DeploymentUtil.failMessage(MPasteMainVerticle.class, err)));
		
		Future.all(core, huertos, mmc, cine, mpaste)
				.onSuccess(_ -> promise.complete())
				.onFailure(promise::fail);

		return promise.future();
	}


	private void initializeDirectories() {        
        File baseDir = new File(this.configManager.getBaseDir());
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }
    
    private void copyDefaultConfig() {
        File configFile = new File(configManager.getConfigFile().getAbsolutePath());
        if (!configFile.exists()) {
            try (InputStream in = MainVerticle.class.getClassLoader().getResourceAsStream("default.properties")) {
                if (in != null) {
                    Files.copy(in, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                	Constants.LOGGER.error(
                			MessageUtil.notFound("Default config", "resources"));
                }
            } catch (IOException e) {
                Constants.LOGGER.error(
                		MessageUtil.failedTo("copy", "default config", e));
            }
        }
    }
    
}
