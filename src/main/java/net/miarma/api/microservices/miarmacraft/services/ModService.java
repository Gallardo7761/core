package net.miarma.api.microservices.miarmacraft.services;

import com.eduardomcb.discord.webhook.WebhookClient;
import com.eduardomcb.discord.webhook.WebhookManager;
import com.eduardomcb.discord.webhook.models.Message;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.miarmacraft.dao.ModDAO;
import net.miarma.api.microservices.miarmacraft.entities.ModEntity;

import java.util.List;

public class ModService {
	private final ModDAO modDAO;
	private final ConfigManager configManager = ConfigManager.getInstance();
	
	public ModService(Pool pool) {
		this.modDAO = new ModDAO(pool);
	}

	private void sendWebhookMessage(Message message) {
		WebhookManager webhookManager = new WebhookManager()
				.setChannelUrl(configManager.getStringProperty("discord.webhook"))
				.setMessage(message);
		webhookManager.setListener(new WebhookClient.Callback() {
			@Override
			public void onSuccess(String response) {
				Constants.LOGGER.info("Webhook sent successfully");
			}

			@Override
			public void onFailure(int statusCode, String errorMessage) {
				Constants.LOGGER.error("Failed to send webhook: {}", errorMessage);
			}
		});
		webhookManager.exec();
	}

	public Future<List<ModEntity>> getAll() {
		return modDAO.getAll();
	}
	
	public Future<List<ModEntity>> getAll(QueryParams params) {
		return modDAO.getAll(params);
	}
	
	public Future<ModEntity> getById(Integer id) {
		return modDAO.getById(id).compose(mod -> {
			if (mod == null) {
				return Future.failedFuture(new NotFoundException("Mod with id " + id));
			}
			return Future.succeededFuture(mod);
		});
	}
	
	public Future<ModEntity> update(ModEntity mod) {
		return modDAO.update(mod);
	}
	
	public Future<ModEntity> create(ModEntity mod) {
		return modDAO.insert(mod).compose(createdMod -> {
			Message message = new Message()
					.setContent("Se ha añadido el mod **" + createdMod.getName() + "** a la lista @everyone");
			sendWebhookMessage(message);
			return Future.succeededFuture(createdMod);
		});
	}

	public Future<Boolean> delete(Integer id) {
		return getById(id).compose(mod -> {
			if (mod == null) {
				return Future.failedFuture(new NotFoundException("Mod with id " + id));
			}
			Message message = new Message()
					.setContent("Se ha eliminado el mod **" + mod.getName() + "** de la lista @everyone");
			sendWebhookMessage(message);
			return modDAO.delete(id);
		});
	}
}
