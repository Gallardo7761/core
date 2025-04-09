package net.miarma.api.core.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.OSType;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.core.dao.FileDAO;
import net.miarma.api.core.entities.FileEntity;
import net.miarma.api.util.MessageUtil;

public class FileService {

    private final FileDAO fileDAO;

    public FileService(Pool pool) {
        this.fileDAO = new FileDAO(pool);
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

    public Future<FileEntity> create(FileEntity file, byte[] fileBinary) {
    	String dir = ConfigManager.getInstance()
    			.getFilesDir(file.getContext().toCtxString());
    	
    	System.out.println(dir + file.getFile_name());
    	
    	String pathString = dir + file.getFile_name();
        Path filePath = Paths.get(dir + file.getFile_name());
        file.setFile_path(ConfigManager.getOS() == OSType.WINDOWS ?
        		pathString.replace("\\", "\\\\") : pathString);

        try {
            Path parent = filePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            Files.write(filePath, fileBinary);
        } catch (IOException e) {
            e.printStackTrace();
            return Future.failedFuture(e);
        }

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