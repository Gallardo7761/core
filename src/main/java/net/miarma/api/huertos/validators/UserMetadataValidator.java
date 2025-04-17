package net.miarma.api.huertos.validators;

import io.vertx.core.Future;
import net.miarma.api.common.validation.ValidationResult;
import net.miarma.api.huertos.entities.UserMetadataEntity;

public class UserMetadataValidator {

    public Future<ValidationResult> validate(UserMetadataEntity meta) {
        ValidationResult result = new ValidationResult();

        if (meta == null) {
            result.addError("metadata", "Los metadatos no pueden ser nulos");
            return Future.succeededFuture(result);
        }

        if (meta.getUser_id() == null)
            result.addError("user_id", "El ID de usuario es obligatorio");

        if (meta.getMember_number() == null || meta.getMember_number() <= 0)
            result.addError("member_number", "El número de miembro debe ser mayor que 0");

        if (meta.getPlot_number() == null || meta.getPlot_number() <= 0)
            result.addError("plot_number", "El número de parcela debe ser mayor que 0");

        if (meta.getDni() == null || meta.getDni().isBlank())
            result.addError("dni", "El DNI es obligatorio");

        if (meta.getPhone() == null || meta.getPhone() <= 0)
            result.addError("phone", "El número de teléfono debe ser válido");

        if (meta.getType() == null)
            result.addError("type", "El tipo de usuario es obligatorio");

        if (meta.getStatus() == null)
            result.addError("status", "El estado del usuario es obligatorio");

        if (meta.getRole() == null)
            result.addError("role", "El rol del usuario es obligatorio");

        return Future.succeededFuture(result);
    }
} 
