package net.miarma.api.core.validators;

import io.vertx.core.Future;
import net.miarma.api.common.validation.ValidationResult;
import net.miarma.api.core.entities.FileEntity;

public class FileValidator {

    public Future<ValidationResult> validate(FileEntity file, int size) {
        ValidationResult result = new ValidationResult();

        if (file == null) {
            return Future.succeededFuture(result.addError("file", "El archivo no puede ser nulo"));
        }

        if (file.getFile_name() == null || file.getFile_name().isBlank()) {
            result.addError("file_name", "El nombre del archivo es obligatorio");
        }
        
        if (file.getMime_type() == null || file.getMime_type().isBlank()) {
            result.addError("mime_type", "El tipo MIME es obligatorio");
        }

        if (file.getContext() == null) {
            result.addError("context", "El contexto del archivo es obligatorio");
        }

        if (file.getUploaded_by() == null || file.getUploaded_by() <= 0) {
            result.addError("uploaded_by", "El ID del usuario que subió el archivo es obligatorio y debe ser válido");
        }
        
        if (size <= 0) {
			result.addError("size", "El archivo debe pesar más de 0 bytes");
		}
        
        if (file.getFile_name() != null && file.getFile_name().length() > 255) {
			result.addError("file_name", "El nombre del archivo es demasiado largo");
		}
        
        if (size > 10485760) { // 10 MB limit
			result.addError("size", "El archivo no puede pesar más de 10 MB");
		}

        return Future.succeededFuture(result);
    }
    
    public Future<ValidationResult> validate(FileEntity file) {
        ValidationResult result = new ValidationResult();

        if (file == null) {
            return Future.succeededFuture(result.addError("file", "File cannot be null"));
        }

        if (file.getFile_name() == null || file.getFile_name().isBlank()) {
            result.addError("file_name", "File name is required");
        }

        if (file.getFile_path() == null || file.getFile_path().isBlank()) {
            result.addError("file_path", "File path is required");
        }

        if (file.getMime_type() == null || file.getMime_type().isBlank()) {
            result.addError("mime_type", "MIME type is required");
        }

        if (file.getContext() == null) {
            result.addError("context", "File context is required");
        }

        if (file.getUploaded_by() == null || file.getUploaded_by() <= 0) {
            result.addError("uploaded_by", "Uploader user ID is required and must be valid");
        }
        
        if (file.getFile_name() != null && file.getFile_name().length() > 255) {
			result.addError("file_name", "File name is too long");
		}
        
        if (file.getFile_path() != null && file.getFile_path().length() > 255) {
			result.addError("file_path", "File path is too long");
        }
        
        return Future.succeededFuture(result);
    }
}
