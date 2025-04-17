package net.miarma.api.common.validation;

import io.vertx.core.Future;

public interface Validator<T> {
    Future<ValidationResult> validate(T entity);
}
