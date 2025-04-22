package net.miarma.api.core.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.OSType;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.exceptions.ValidationException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.core.dao.FileDAO;
import net.miarma.api.core.entities.FileEntity;
import net.miarma.api.core.validators.FileValidator;

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
        return fileDAO.getAll().map(files ->
            files.stream()
                .filter(file -> file.getFile_id().equals(id))
                .findFirst()
                .orElse(null)
        ).compose(file -> {
            if (file == null) {
                return Future.failedFuture(new NotFoundException("File not found"));
            }
            return Future.succeededFuture(file);
        });
    }

    public Future<List<FileEntity>> getUserFiles(Integer userId) {
        return fileDAO.getAll().map(files ->
            files.stream()
                .filter(file -> file.getUploaded_by().equals(userId))
                .toList()
        );
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

    public Future<Void> delete(Integer fileId) {
        return fileDAO.delete(fileId).mapEmpty();
    }
}