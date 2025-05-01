package net.miarma.api.miarmacraft.handlers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.miarmacraft.entities.ModEntity;
import net.miarma.api.miarmacraft.services.ModService;
import net.miarma.api.util.JsonUtil;

public class ModDataHandler {
	ModService modService;
	
	public ModDataHandler(Pool pool) {
		this.modService = new ModService(pool);
	}
	
	public void getAll(RoutingContext ctx) {
		modService.getAll()
			.onSuccess(mods -> JsonUtil.sendJson(ctx, ApiStatus.OK, mods))
			.onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
	}
	
	public void getById(RoutingContext ctx) {
		Integer modId = Integer.parseInt(ctx.pathParam("mod_id"));
		modService.getById(modId)
			.onSuccess(mod -> JsonUtil.sendJson(ctx, ApiStatus.OK, mod))
			.onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
	}
	
	public void create(RoutingContext ctx) {
		try {
			String jsonData = ctx.request().getFormAttribute("data");
			if (jsonData == null) {
				JsonUtil.sendJson(ctx, ApiStatus.BAD_REQUEST, null, "Falta el campo 'data' con los datos del mod");
				return;
			}

			ModEntity mod = Constants.GSON.fromJson(jsonData, ModEntity.class);
			if (mod == null) {
				JsonUtil.sendJson(ctx, ApiStatus.BAD_REQUEST, null, "JSON del mod inválido");
				return;
			}

			var fileUploadOpt = ctx.fileUploads().stream().findFirst();
			if (fileUploadOpt.isEmpty()) {
				JsonUtil.sendJson(ctx, ApiStatus.BAD_REQUEST, null, "Falta el archivo .jar del mod");
				return;
			}

			var fileUpload = fileUploadOpt.get();
			String originalFileName = fileUpload.fileName();
			if (!originalFileName.endsWith(".jar")) {
				JsonUtil.sendJson(ctx, ApiStatus.BAD_REQUEST, null, "Solo se permiten archivos .jar");
				return;
			}

			String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
			String modsDir = ConfigManager.getInstance().getModsDir();
			Path tempPath = Paths.get(fileUpload.uploadedFileName());
			Path modsDirPath = Paths.get(modsDir);
			Path targetPath = modsDirPath.resolve(sanitizedFileName);

			Files.createDirectories(modsDirPath);
			
			Files.move(tempPath, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

			mod.setUrl("/files/miarmacraft/mods/" + sanitizedFileName);

			modService.create(mod)
				.onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
				.onFailure(err -> {
					err.printStackTrace();
					JsonUtil.sendJson(ctx, ApiStatus.INTERNAL_SERVER_ERROR, null, err.getMessage());
				});

		} catch (Exception e) {
			e.printStackTrace();
			JsonUtil.sendJson(ctx, ApiStatus.BAD_REQUEST, null, "Error procesando la petición: " + e.getMessage());
		}
	}
	
	public void update(RoutingContext ctx) {
		ModEntity mod = Constants.GSON.fromJson(ctx.body().asString(), ModEntity.class);
		modService.update(mod)
			.onSuccess(_ -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
			.onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
	}
	
	public void delete(RoutingContext ctx) {
		Integer modId = Integer.parseInt(ctx.pathParam("mod_id"));

		modService.getById(modId).onSuccess(mod -> {
			String modsDir = ConfigManager.getInstance().getModsDir();
			String filename = Paths.get(mod.getUrl()).getFileName().toString();
			Path fullPath = Paths.get(modsDir, filename);

			ctx.vertx().fileSystem().delete(fullPath.toString(), fileRes -> {
				if (fileRes.failed()) {
					Constants.LOGGER.warn("No se pudo eliminar el archivo del mod: " + fullPath, fileRes.cause());
				}

				modService.delete(modId)
					.onSuccess(_ -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
					.onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
			});
		}).onFailure(err -> {
			JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage());
		});
	}

}
