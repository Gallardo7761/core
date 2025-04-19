package net.miarma.api.huertos.validators;

import io.vertx.core.Future;
import net.miarma.api.common.validation.ValidationResult;
import net.miarma.api.huertos.entities.ExpenseEntity;

public class ExpenseValidator {

    public Future<ValidationResult> validate(ExpenseEntity expense) {
        ValidationResult result = new ValidationResult();

        if (expense == null) {
            result.addError("expense", "La entidad no puede ser nula");
            return Future.succeededFuture(result);
        }

        if (expense.getConcept() == null || expense.getConcept().isBlank()) {
            result.addError("concept", "El concepto es obligatorio");
        }

        if (expense.getAmount() == null) {
            result.addError("amount", "El importe es obligatorio");
        } else if (expense.getAmount().signum() < 0) {
            result.addError("amount", "El importe no puede ser negativo");
        }

        if(expense.getSupplier() == null || expense.getSupplier().isBlank()) {
			result.addError("supplier", "El proveedor es obligatorio");
		} else if (expense.getSupplier() != null && expense.getSupplier().length() > 255) {
            result.addError("supplier", "El nombre del proveedor es demasiado largo");
        }

        if(expense.getInvoice() == null || expense.getInvoice().isBlank()) {
			result.addError("invoice", "El nombre de la factura es obligatorio");
        } else if (expense.getInvoice() != null && expense.getInvoice().length() > 255) {
            result.addError("invoice", "El nombre de la factura es demasiado largo");
        }

        if (expense.getType() == null) {
            result.addError("type", "El tipo de pago es obligatorio");
        }

        return Future.succeededFuture(result);
    }
}
