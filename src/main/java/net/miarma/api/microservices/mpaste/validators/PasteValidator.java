package net.miarma.api.microservices.mpaste.validators;

import io.vertx.core.Future;
import net.miarma.api.common.validation.ValidationResult;
import net.miarma.api.microservices.mpaste.entities.PasteEntity;

public class PasteValidator {
	public Future<ValidationResult> validate(PasteEntity entity) {
		ValidationResult result = new ValidationResult();
		
        if (entity.getTitle() == null || entity.getTitle().trim().isEmpty()) {
            result.addError("title", "El título no puede estar vacío");
        }

        if (entity.getContent() == null || entity.getContent().trim().isEmpty()) {
            result.addError("content", "El contenido no puede estar vacío");
        }

        if (Boolean.TRUE.equals(entity.getIs_private())) {
            if (entity.getPassword() == null || entity.getPassword().trim().isEmpty()) {
                result.addError("password", "Las pastes privadas requieren contraseña");
            }
        }

        if (entity.getTitle() != null && entity.getTitle().length() > 128) {
            result.addError("title", "Título demasiado largo (128 caracteres máx.)");
        }
				
		return Future.succeededFuture(result);	
	}
}
