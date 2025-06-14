package net.miarma.api.common.validation;

import io.vertx.core.Future;

/**
 * Interfaz para la validación de entidades.
 * @param <T> Tipo de entidad a validar.
 * @author José Manuel Amador Gallardo
 */
public interface Validator<T> {
    Future<ValidationResult> validate(T entity);
}
