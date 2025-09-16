package net.miarma.api.microservices.mpaste.services;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.exceptions.NotFoundException;
import net.miarma.api.common.exceptions.UnauthorizedException;
import net.miarma.api.common.exceptions.ValidationException;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.common.security.PasswordHasher;
import net.miarma.api.microservices.mpaste.dao.PasteDAO;
import net.miarma.api.microservices.mpaste.entities.PasteEntity;
import net.miarma.api.microservices.mpaste.validators.PasteValidator;
import net.miarma.api.util.PasteKeyGenerator;

public class PasteService {

    private final PasteDAO pasteDAO;
    private final PasteValidator pasteValidator;

    public PasteService(Pool pool) {
        this.pasteDAO = new PasteDAO(pool);
        this.pasteValidator = new PasteValidator();
    }

    public Future<List<PasteEntity>> getAll() {
        return pasteDAO.getAll().compose(pastes -> {
        	List<PasteEntity> publicPastes = pastes.stream()
                    .filter(p -> !Boolean.TRUE.equals(p.getIs_private()))
                    .toList();
    		return Future.succeededFuture(publicPastes);
        });
    }

    public Future<List<PasteEntity>> getAll(QueryParams params) {
        return pasteDAO.getAll(params).compose(pastes -> {
        	List<PasteEntity> publicPastes = pastes.stream()
                    .filter(p -> !Boolean.TRUE.equals(p.getIs_private()))
                    .toList();
    		return Future.succeededFuture(publicPastes);
        });
    }

    public Future<PasteEntity> getById(Long id) {
        return pasteDAO.getById(id);
    }
    
    public Future<PasteEntity> getByKey(String key, String password) {
    	return pasteDAO.getByKey(key).compose(paste -> {
            if (paste == null) {
                return Future.failedFuture(new NotFoundException("Paste with key " + key));
            }
            if (Boolean.TRUE.equals(paste.getIs_private())) {
                if (password == null || !PasswordHasher.verify(password, paste.getPassword())) {
                    return Future.failedFuture(new UnauthorizedException("Contraseña incorrecta"));
                }
            }
            if (Boolean.TRUE.equals(paste.getBurn_after())) {
                Vertx.vertx().setTimer(5000, _ -> {
                	pasteDAO.delete(paste.getPaste_id());
                });
            }
            return Future.succeededFuture(paste);
        });
    }

    public Future<PasteEntity> create(PasteEntity paste) {
    	return pasteValidator.validate(paste).compose(validation -> {
            if (!validation.isValid()) {
                return Future.failedFuture(new ValidationException(Constants.GSON.toJson(validation.getErrors())));
            }
            
            String key = PasteKeyGenerator.generate(6);
            paste.setPaste_key(key);
            
            if (paste.getPassword() != null) {
                paste.setPassword(PasswordHasher.hash(paste.getPassword()));
            }
            
            return pasteDAO.existsByKey(key).compose(exists -> {
                if (exists) {
                    return create(paste); // recursivo, genera otra clave
                }
                return pasteDAO.insert(paste);
            });
    	});
    }

    public Future<PasteEntity> update(PasteEntity paste) { // nope
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

    public Future<Boolean> exists(String key) { 
        return pasteDAO.existsByKey(key);
    }
}
