package net.miarma.api.huertos.validators;

import io.vertx.core.Future;
import net.miarma.api.common.security.DNIValidator;
import net.miarma.api.common.validation.ValidationResult;
import net.miarma.api.huertos.entities.PreUserEntity;

public class PreUserValidator {

    public Future<ValidationResult> validate(PreUserEntity preUser) {
        ValidationResult result = new ValidationResult();

        if (preUser == null) {
            result.addError("preUser", "El preusuario no puede ser nulo");
            return Future.succeededFuture(result);
        }

        if (preUser.getDni() == null || preUser.getDni().isBlank()) {
            result.addError("dni", "El DNI es obligatorio");
        } else if (!DNIValidator.isValid(preUser.getDni())) {
            result.addError("dni", "El DNI no es válido");
        }

        if (preUser.getDisplay_name() == null || preUser.getDisplay_name().isBlank()) {
            result.addError("display_name", "El nombre es obligatorio");
        }

        if (preUser.getUser_name() == null || preUser.getUser_name().isBlank()) {
            result.addError("user_name", "El nombre de usuario es obligatorio");
        }

        if (preUser.getEmail() != null && !preUser.getEmail().isBlank()) {
			if (!preUser.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
				result.addError("email", "El correo electrónico no es válido");
			}
		}
        
        if (preUser.getPhone() == null || preUser.getPhone() <= 0 || 
        		preUser.getPhone().toString().length() != 9) {
            result.addError("phone", "El teléfono es obligatorio y debe ser válido");
        }

        if (preUser.getAddress() == null || preUser.getAddress().isBlank()) {
            result.addError("address", "La dirección es obligatoria");
        }

        if (preUser.getZip_code() == null || preUser.getZip_code().isBlank() || preUser.getZip_code().length() != 5) {
            result.addError("zip_code", "El código postal es obligatorio");
        }

        if (preUser.getCity() == null || preUser.getCity().isBlank()) {
            result.addError("city", "La ciudad es obligatoria");
        }

        if (preUser.getType() == null) {
            result.addError("type", "El tipo de usuario es obligatorio");
        }

        if (preUser.getStatus() == null) {
            result.addError("status", "El estado del usuario es obligatorio");
        }

        if (preUser.getRequest_id() == null) {
            result.addError("request_id", "El ID de solicitud es obligatorio");
        }

        return Future.succeededFuture(result);
    }
}
