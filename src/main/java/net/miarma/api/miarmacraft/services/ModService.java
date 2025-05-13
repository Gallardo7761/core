package net.miarma.api.miarmacraft.services;

import java.util.List;

import com.eduardomcb.discord.webhook.WebhookClient;
import com.eduardomcb.discord.webhook.WebhookManager;
import com.eduardomcb.discord.webhook.models.Message;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.miarmacraft.dao.ModDAO;
import net.miarma.api.miarmacraft.entities.ModEntity;

public class ModService {
	private final ModDAO modDAO;
	
	public ModService(Pool pool) {
		this.modDAO = new ModDAO(pool);
	}
	
	public Future<List<ModEntity>> getAll() {
		return modDAO.getAll();
	}
	
	public Future<List<ModEntity>> getAll(QueryParams params) {
		return modDAO.getAll(params);
	}
	
	public Future<ModEntity> getById(Integer id) {
		return getAll().compose(mods -> {
			ModEntity mod = mods.stream()
	                .filter(m -> m.getMod_id().equals(id))
	                .findFirst()
	                .orElse(null);
	            return mod != null ?
	                Future.succeededFuture(mod) :
	                Future.failedFuture(new NotFoundException("Mod with id " + id));
		});
	}
	
	public Future<ModEntity> update(ModEntity mod) {
		return modDAO.update(mod);
	}
	
	public Future<ModEntity> create(ModEntity mod) {
		return modDAO.insert(mod).compose(createdMod -> {
			Message message = new Message()
					.setContent("Se ha añadido el mod **" + createdMod.getName() + "** a la lista @everyone");
			WebhookManager webhookManager = new WebhookManager()
					.setChannelUrl(Constants.WEBHOOK_URL)
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
			return Future.succeededFuture(createdMod);
		});
	}
	
	public Future<ModEntity> delete(Integer id) {
		return getById(id).compose(mod -> {
			if (mod == null) {
				return Future.failedFuture(new NotFoundException("Mod with id " + id));
			}
			Message message = new Message()
					.setContent("Se ha eliminado el mod **" + mod.getName() + "** de la lista @everyone");
			WebhookManager webhookManager = new WebhookManager()
					.setChannelUrl(Constants.WEBHOOK_URL)
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
			return modDAO.delete(id);
		});
	}
}
