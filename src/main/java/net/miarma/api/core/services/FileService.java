package net.miarma.api.core.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants.CoreFileContext;
import net.miarma.api.common.QueryFilters;
import net.miarma.api.core.dao.FileDAO;
import net.miarma.api.core.entities.FileEntity;
import net.miarma.api.util.MessageUtil;

public class FileService {

    private final FileDAO fileDAO;

    public FileService(Pool pool) {
        this.fileDAO = new FileDAO(pool);
    }

    public Future<List<FileEntity>> getAll(QueryFilters filters) {
        return fileDAO.getAll(filters);
    }

    public Future<FileEntity> getById(Integer id) {
        return fileDAO.getAll().map(files ->
            files.stream()
                .filter(file -> file.getFile_id().equals(id))
                .findFirst()
                .orElse(null)
        ).compose(file -> {
            if (file == null) {
                return Future.failedFuture(MessageUtil.notFound("File", "in the storage"));
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

    public Future<FileEntity> create(FileEntity file) {
        return fileDAO.insert(file);
    }

    public Future<FileEntity> create(JsonObject body) {
        byte[] fileBinary = body.getBinary("file");
        Path filePath = Paths.get(body.getString("file_path"));

        try {
            Files.write(filePath, fileBinary);
        } catch (IOException e) {
            return Future.failedFuture(e);
        }

        FileEntity file = new FileEntity();
        file.setFile_name(body.getString("file_name"));
        file.setFile_path(body.getString("file_path"));
        file.setMime_type(body.getString("mime_type"));
        file.setUploaded_by(body.getInteger("uploaded_by"));
        file.setContext(CoreFileContext.fromInt(body.getInteger("context")));

        return fileDAO.insert(file);
    }

    public Future<FileEntity> downloadFile(Integer fileId) {
        return getById(fileId);
    }

    public Future<FileEntity> update(FileEntity file) {
        return fileDAO.update(file);
    }

    public Future<Void> delete(Integer fileId) {
        return fileDAO.delete(fileId).mapEmpty();
    }
}