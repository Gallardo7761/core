package net.miarma.api.common.validation;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa el resultado de una validación, conteniendo errores asociados a campos específicos.
 *
 * @author José Manuel Amador Gallardo
 */
public class ValidationResult {

    private final Map<String, String> errors = new HashMap<>();

    public ValidationResult addError(String field, String message) {
        errors.put(field, message);
        return this;
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public String getFirstError() {
        return errors.values().stream().findFirst().orElse(null);
    }
}
