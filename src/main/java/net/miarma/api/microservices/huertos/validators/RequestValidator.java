package net.miarma.api.microservices.huertos.validators;

import io.vertx.core.Future;
import net.miarma.api.common.Constants.HuertosRequestType;
import net.miarma.api.common.validation.ValidationResult;
import net.miarma.api.microservices.huertos.entities.RequestEntity;

public class RequestValidator {

    public Future<ValidationResult> validate(RequestEntity request) {
        ValidationResult result = new ValidationResult();

        if (request == null) {
            result.addError("request", "La solicitud no puede ser nula");
            return Future.succeededFuture(result);
        }

        if (request.getType() == null) {
            result.addError("type", "El tipo de solicitud es obligatorio");
        }

        if (request.getStatus() == null) {
            result.addError("status", "El estado de la solicitud es obligatorio");
        }

        if (request.getRequested_by() == null && request.getType() != HuertosRequestType.REGISTER) {
            result.addError("requested_by", "El solicitante es obligatorio");
        }

        return Future.succeededFuture(result);
    }
}
