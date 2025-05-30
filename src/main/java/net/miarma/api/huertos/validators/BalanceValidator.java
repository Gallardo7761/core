package net.miarma.api.huertos.validators;

import io.vertx.core.Future;
import net.miarma.api.common.validation.ValidationResult;
import net.miarma.api.common.validation.Validator;
import net.miarma.api.huertos.entities.BalanceEntity;

import java.math.BigDecimal;

public class BalanceValidator implements Validator<BalanceEntity> {

    @Override
    public Future<ValidationResult> validate(BalanceEntity balance) {
        ValidationResult result = new ValidationResult();

        if (balance == null) {
            result.addError("balance", "Balance data is null");
            return Future.succeededFuture(result);
        }

        if (balance.getInitial_bank() == null) {
            result.addError("initial_bank", "Debe proporcionar el saldo inicial en banco.");
        } else if (balance.getInitial_bank().compareTo(BigDecimal.ZERO) < 0) {
            result.addError("initial_bank", "El saldo inicial en banco no puede ser negativo.");
        }

        if (balance.getInitial_cash() == null) {
            result.addError("initial_cash", "Debe proporcionar el saldo inicial en efectivo.");
        } else if (balance.getInitial_cash().compareTo(BigDecimal.ZERO) < 0) {
            result.addError("initial_cash", "El saldo inicial en efectivo no puede ser negativo.");
        }

        return Future.succeededFuture(result);
    }
}
