package net.miarma.api.microservices.core.services;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.OSType;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.exceptions.ValidationException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.core.dao.FileDAO;
import net.miarma.api.microservices.core.entities.FileEntity;
import net.miarma.api.microservices.core.validators.FileValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileService {

    private final FileDAO fileDAO;
    private final FileValidator fileValidator;

    public FileService(Pool pool) {
        this.fileDAO = new FileDAO(pool);
        this.fileValidator = new FileValidator();
    }

    public Future<List<FileEntity>> getAll(QueryParams params) {
        return fileDAO.getAll(params);
    }

    public Future<FileEntity> getById(Integer id) {
        return fileDAO.getById(id).compose(file -> {
            if (file == null) {
                return Future.failedFuture(new NotFoundException("File not found with id: " + id));
            }
            return Future.succeededFuture(file);
        });
    }

    public Future<List<FileEntity>> getUserFiles(Integer userId) {
        return fileDAO.getUserFiles(userId);
    }

    public Future<FileEntity> create(FileEntity file, byte[] fileBinary) {
        return fileValidator.validate(file, fileBinary.length).compose(validation -> {
            if (!validation.isValid()) {
                return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
            }

            String dir = ConfigManager.getInstance()
                .getFilesDir(file.getContext().toCtxString());

            String pathString = dir + file.getFile_name();
            Path filePath = Paths.get(dir + file.getFile_name());
            file.setFile_path(ConfigManager.getOS() == OSType.WINDOWS ?
                pathString.replace("\\", "\\\\") : pathString);

            try {
                Files.write(filePath, fileBinary);
            } catch (IOException e) {
                Constants.LOGGER.error("Error writing file to disk: ", e);
                return Future.failedFuture(e);
            }

            return fileDAO.insert(file);
        });
    }

    public Future<FileEntity> downloadFile(Integer fileId) {
        return getById(fileId);
    }

    public Future<FileEntity> update(FileEntity file) {
        return fileValidator.validate(file).compose(validation -> {
            if (!validation.isValid()) {
                return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
            }

            return fileDAO.update(file);
        });
    }

    public Future<FileEntity> upsert(FileEntity file) {
        return fileValidator.validate(file).compose(validation -> {
            if (!validation.isValid()) {
                return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
            }

            return fileDAO.upsert(file, "file_id");
        });
    }

    public Future<Boolean> delete(Integer fileId) {
        return getById(fileId).compose(file -> {
            String dir = ConfigManager.getInstance()
                .getFilesDir(file.getContext().toCtxString());

            String filePath = dir + file.getFile_name();
            Path path = Paths.get(filePath);

            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                Constants.LOGGER.error("Error deleting file from disk: ", e);
                return Future.failedFuture(e);
            }

            return fileDAO.delete(fileId).compose(deleted -> {
                if (!deleted) {
                    return Future.failedFuture(new NotFoundException("File not found with id: " + fileId));
                }
                return Future.succeededFuture(true);
            });
        });
    }

    public Future<Boolean> exists(Integer fileId) {
        return fileDAO.exists(fileId);
    }
}