package net.miarma.api.huertos.validators;

import io.vertx.core.Future;
import net.miarma.api.common.validation.ValidationResult;
import net.miarma.api.huertos.entities.IncomeEntity;

public class IncomeValidator {

    public Future<ValidationResult> validate(IncomeEntity income) {
        ValidationResult result = new ValidationResult();

        if (income == null) {
            result.addError("income", "La entidad no puede ser nula");
            return Future.succeededFuture(result);
        }

        if (income.getMember_number() == null || income.getMember_number() <= 0) {
            result.addError("member_number", "El número de socio es obligatorio y debe ser mayor que 0");
        }

        if (income.getConcept() == null || income.getConcept().isBlank()) {
            result.addError("concept", "El concepto es obligatorio");
        }

        if (income.getAmount() == null) {
            result.addError("amount", "El importe es obligatorio");
        } else if (income.getAmount().signum() < 0) {
            result.addError("amount", "El importe no puede ser negativo");
        }

        if (income.getType() == null) {
            result.addError("type", "El tipo de pago es obligatorio");
        }

        if (income.getFrequency() == null) {
            result.addError("frequency", "La frecuencia del pago es obligatoria");
        }

        return Future.succeededFuture(result);
    }
}
