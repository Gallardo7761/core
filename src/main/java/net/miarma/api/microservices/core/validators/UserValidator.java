package net.miarma.api.microservices.core.validators;

import io.vertx.core.Future;
import net.miarma.api.common.validation.ValidationResult;
import net.miarma.api.microservices.core.entities.UserEntity;

public class UserValidator {

    public Future<ValidationResult> validate(UserEntity user) {
        ValidationResult result = new ValidationResult();

        if (user == null) {
            return Future.succeededFuture(result.addError("user", "El usuario no puede ser nulo"));
        }

        if (user.getUser_name() == null || user.getUser_name().isBlank()) {
            result.addError("user_name", "El nombre de usuario es obligatorio");
        }

        if (user.getDisplay_name() == null || user.getDisplay_name().isBlank()) {
            result.addError("display_name", "El nombre para mostrar es obligatorio");
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
			if (!user.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
				result.addError("email", "El correo electrónico no es válido");
			}
		}

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            result.addError("password", "La contraseña es obligatoria");
        }

        if (user.getGlobal_status() == null) {
            result.addError("global_status", "El estado global del usuario es obligatorio");
        }

        if (user.getRole() == null) {
            result.addError("role", "El rol del usuario es obligatorio");
        }

        return Future.succeededFuture(result);
    }
}
