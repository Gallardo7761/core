package net.miarma.api.huertos.validators;

import io.vertx.core.Future;
import net.miarma.api.common.security.DNIValidator;
import net.miarma.api.common.validation.ValidationResult;
import net.miarma.api.huertos.entities.MemberEntity;

public class MemberValidator {

    public Future<ValidationResult> validate(MemberEntity member) {
        ValidationResult result = new ValidationResult();

        if (member == null) {
            result.addError("member", "La entidad no puede ser nula");
            return Future.succeededFuture(result);
        }

        if (member.getDni() == null || member.getDni().isBlank()) {
            result.addError("dni", "El DNI es obligatorio");
        } else if (!DNIValidator.isValid(member.getDni())) {
            result.addError("dni", "El DNI no es válido");
        }

        if (member.getDisplay_name() == null || member.getDisplay_name().isBlank()) {
            result.addError("display_name", "El nombre es obligatorio");
        }

        if (member.getPhone() == null || member.getPhone() <= 0) {
            result.addError("phone", "El teléfono es obligatorio y debe ser válido");
        }

        if (member.getUser_name() == null || member.getUser_name().isBlank()) {
            result.addError("user_name", "El nombre de usuario es obligatorio");
        }

        if (member.getType() == null) {
            result.addError("type", "El tipo de usuario es obligatorio");
        }

        if (member.getStatus() == null) {
            result.addError("status", "El estado del usuario es obligatorio");
        }

        if (member.getRole() == null) {
            result.addError("role", "El rol del usuario en Huertos es obligatorio");
        }

        return Future.succeededFuture(result);
    }
}
