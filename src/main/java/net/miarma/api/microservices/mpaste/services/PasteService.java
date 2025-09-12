package net.miarma.api.microservices.mpaste.services;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.microservices.mpaste.dao.PasteDAO;
import net.miarma.api.microservices.mpaste.entities.PasteEntity;

import java.util.List;

public class PasteService {

    private final PasteDAO pasteDAO;

    public PasteService(Pool pool) {
        this.pasteDAO = new PasteDAO(pool);
    }

    public Future<List<PasteEntity>> getAll() {
        return pasteDAO.getAll();
    }

    public Future<List<PasteEntity>> getAll(QueryParams params) {
        return pasteDAO.getAll(params);
    }

    public Future<PasteEntity> getById(Long id) {
        return pasteDAO.getById(id);
    }
    
    public Future<PasteEntity> getByKey(String key) {
    	return pasteDAO.getByKey(key).compose(paste -> {
            if (paste == null) {
                return Future.failedFuture(new NotFoundException("Paste with key " + key));
            }
            return Future.succeededFuture(paste);
        });
    }

    public Future<PasteEntity> create(PasteEntity paste) {
        return pasteDAO.insert(paste);
    }

    public Future<PasteEntity> update(PasteEntity paste) {
        return pasteDAO.update(paste);
    }

    public Future<Boolean> delete(Long id) {
        return getById(id).compose(paste -> {
            if (paste == null) {
                return Future.failedFuture(new NotFoundException("Paste with id " + id));
            }
            return pasteDAO.delete(id);
        });
    }

    public Future<Boolean> exists(Long id) {
        return pasteDAO.exists(id);
    }
}
