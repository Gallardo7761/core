package net.miarma.api.microservices.huertos.validators;

import io.vertx.core.Future;
import net.miarma.api.common.validation.ValidationResult;
import net.miarma.api.microservices.huertos.entities.AnnouncementEntity;

public class AnnouncementValidator {

    public Future<ValidationResult> validate(AnnouncementEntity announce) {
        ValidationResult result = new ValidationResult();

        if (announce == null) {
            result.addError("announce", "El anuncio no puede ser nulo");
            return Future.succeededFuture(result);
        }

        if (announce.getBody() == null || announce.getBody().isBlank()) {
            result.addError("body", "El cuerpo del anuncio es obligatorio");
        } else if (announce.getBody().length() > 1000) {
            result.addError("body", "El cuerpo del anuncio no puede exceder los 1000 caracteres");
        }

        if (announce.getPriority() == null) {
            result.addError("priority", "La prioridad del anuncio es obligatoria");
        }

        if (announce.getPublished_by() == null || announce.getPublished_by() <= 0) {
            result.addError("published_by", "El ID del usuario que publica el anuncio es obligatorio y debe ser mayor que 0");
        }

        return Future.succeededFuture(result);
    }
}
