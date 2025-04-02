package net.miarma.api.core.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants.CoreFileContext;
import net.miarma.api.core.dao.FileDAO;
import net.miarma.api.core.entities.FileEntity;
import net.miarma.api.util.MessageUtil;

public class FileService {
	
	private final FileDAO fileDAO;
	
	public FileService(Pool pool) {
		this.fileDAO = new FileDAO(pool);
	}
	
	public void getAll(Handler<AsyncResult<List<FileEntity>>> handler) {
		fileDAO.getAll(ar -> {
			if (ar.failed()) {
                handler.handle(Future.failedFuture(ar.cause()));
                return;
            }
            handler.handle(Future.succeededFuture(ar.result()));
		});
	}
	
	public void getById(Integer id, Handler<AsyncResult<FileEntity>> handler) {
		fileDAO.getAll(ar -> {
			if (ar.failed()) {
                handler.handle(Future.failedFuture(ar.cause()));
                return;
            }

            ar.result().stream()
                .filter(file -> file.getFile_id().equals(id))
                .findFirst()
                .ifPresentOrElse(
                    user -> handler.handle(Future.succeededFuture(user)),
                    () -> handler.handle(Future.failedFuture(MessageUtil.notFound("File", "in the storage")))
                );
		});
	}
	
	public void getUserFiles(Integer userId, Handler<AsyncResult<List<FileEntity>>> handler) {
		fileDAO.getAll(ar -> {
			if (ar.failed()) {
				handler.handle(Future.failedFuture(ar.cause()));
				return;
			}

			List<FileEntity> files = ar.result().stream()
				.filter(file -> file.getUploaded_by().equals(userId))
				.toList();

			handler.handle(Future.succeededFuture(files));
		});
	}
	
	public void create(FileEntity file, Handler<AsyncResult<FileEntity>> handler) {
		fileDAO.insert(file, ar -> {
			if (ar.failed()) {
				handler.handle(Future.failedFuture(ar.cause()));
				return;
			}
			handler.handle(Future.succeededFuture(ar.result()));
		});
	}
	
	public void create(JsonObject body, Handler<AsyncResult<FileEntity>> handler) {
		byte[] fileBinary = body.getBinary("file");
		Path filePath = Paths.get(body.getString("file_path"));
		
		// Save file to disk
		try {
			Files.write(filePath, fileBinary);
		} catch (IOException e) {
			handler.handle(Future.failedFuture(e));
			return;
		}
		
		FileEntity file = new FileEntity();
		file.setFile_name(body.getString("file_name"));
		file.setFile_path(body.getString("file_path"));
		file.setMime_type(body.getString("mime_type"));		
		file.setUploaded_by(body.getInteger("uploaded_by"));
		file.setContext(CoreFileContext.fromInt(body.getInteger("context")));
		
		fileDAO.insert(file, ar -> {
			if (ar.failed()) {
				handler.handle(Future.failedFuture(ar.cause()));
				return;
			}
			handler.handle(Future.succeededFuture(ar.result()));
		});		
	}
	
	public void downloadFile(Integer fileId, Handler<AsyncResult<FileEntity>> handler) {
		fileDAO.getAll(ar -> {
			if (ar.failed()) {
				handler.handle(Future.failedFuture(ar.cause()));
				return;
			}

			ar.result().stream()
				.filter(file -> file.getFile_id().equals(fileId))
				.findFirst()
				.ifPresentOrElse(
					file -> handler.handle(Future.succeededFuture(file)),
					() -> handler.handle(Future.failedFuture(MessageUtil.notFound("File", "in the storage")))
				);
		});
	}
	
	public void update(FileEntity file, Handler<AsyncResult<FileEntity>> handler) {
		fileDAO.update(file, ar -> {
			if (ar.failed()) {
				handler.handle(Future.failedFuture(ar.cause()));
				return;
			}
			handler.handle(Future.succeededFuture(ar.result()));
		});
	}
	
	public void delete(Integer fileId, Handler<AsyncResult<Void>> handler) {
		fileDAO.delete(fileId, ar -> {
			if (ar.failed()) {
				handler.handle(Future.failedFuture(ar.cause()));
				return;
			}
			handler.handle(Future.succeededFuture());
		});
	}
	
}
