package net.miarma.api.core.handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.Constants.CoreFileContext;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.core.entities.FileEntity;
import net.miarma.api.core.services.FileService;
import net.miarma.api.util.JsonUtil;

@SuppressWarnings("unused")
public class FileDataHandler {

    private final FileService fileService;

    public FileDataHandler(Pool pool) {
        this.fileService = new FileService(pool);
    }

    public void getAll(RoutingContext ctx) {
        QueryParams params = QueryParams.from(ctx);

        fileService.getAll(params)
            .onSuccess(files -> JsonUtil.sendJson(ctx, ApiStatus.OK, files))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void getById(RoutingContext ctx) {
        Integer fileId = Integer.parseInt(ctx.pathParam("file_id"));

        fileService.getById(fileId)
            .onSuccess(file -> JsonUtil.sendJson(ctx, ApiStatus.OK, file))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void create(RoutingContext ctx) {
    	try {
            String fileName = ctx.request().getFormAttribute("file_name");
            String mimeType = ctx.request().getFormAttribute("mime_type");
            int uploadedBy = Integer.parseInt(ctx.request().getFormAttribute("uploaded_by"));
            int contextValue = Integer.parseInt(ctx.request().getFormAttribute("context"));

            FileUpload upload = ctx.fileUploads().stream()
                .filter(f -> f.name().equals("file"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

            Buffer buffer = ctx.vertx().fileSystem().readFileBlocking(upload.uploadedFileName());
            byte[] fileBinary = buffer.getBytes();

            FileEntity file = new FileEntity();
            file.setFile_name(fileName);
            file.setMime_type(mimeType);
            file.setUploaded_by(uploadedBy);
            file.setContext(CoreFileContext.fromInt(contextValue));

            fileService.create(file, fileBinary)
                .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
                .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendJson(ctx, ApiStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
        }
    }


    public void update(RoutingContext ctx) {
        FileEntity file = Constants.GSON.fromJson(ctx.body().asString(), FileEntity.class);

        fileService.update(file)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.OK, result))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void delete(RoutingContext ctx) {
        Integer fileId = Integer.parseInt(ctx.pathParam("file_id"));
        JsonObject body = ctx.body().asJsonObject();
        String filePath = body.getString("file_path");
        
        try {
        	Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
        	e.printStackTrace();
			JsonUtil.sendJson(ctx, ApiStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
			return;
        }
        
        fileService.delete(fileId)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }
}  
