package net.miarma.api.common.db;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import net.miarma.api.common.Constants;
import net.miarma.api.common.ValuableEnum;
import net.miarma.api.common.annotations.APIDontReturn;

import java.lang.reflect.Field;

/**
 * Clase base para todas las entidades persistentes del sistema.
 * <p>
 * Proporciona utilidades para:
 * <ul>
 *     <li>Construir una entidad a partir de una fila de base de datos ({@link Row})</li>
 *     <li>Serializar una entidad a {@link JsonObject}</li>
 *     <li>Generar una representación en texto</li>
 * </ul>
 *
 * Los campos se mapean por reflexión, lo que permite extender fácilmente las entidades
 * sin necesidad de escribir lógica de parsing repetitiva.
 *
 * @author José Manuel Amador Gallardo
 */
public abstract class AbstractEntity {

    /**
     * Constructor por defecto. Requerido para instanciación sin datos.
     */
	public AbstractEntity() {}

    /**
     * Constructor que inicializa los campos de la entidad a partir de una fila de base de datos.
     *
     * @param row Fila SQL proporcionada por Vert.x.
     */
    public AbstractEntity(Row row) {
        populateFromRow(row);
    }

    /**
     * Rellena los campos del objeto usando reflexión a partir de una {@link Row} de Vert.x.
     * Se soportan tipos básicos (String, int, boolean, etc.), enums con método estático {@code fromInt(int)},
     * y {@link java.math.BigDecimal} (a través del tipo {@code Numeric} de Vert.x).
     * <p>
     * Si un tipo no está soportado, se registra un error en el log y se ignora ese campo.
     *
     * @param row Fila de datos de la que extraer los valores.
     */
    private void populateFromRow(Row row) {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Class<?> type = field.getType();
                String name = field.getName();

                Object value;
                if (type.isEnum()) {
                    Integer intValue = row.getInteger(name);
                    if (intValue != null) {
                        try {
                            var method = type.getMethod("fromInt", int.class);
                            value = method.invoke(null, intValue);
                        } catch (Exception e) {
                            value = null;
                        }
                    } else {
                        value = null;
                    }
                } else {
                	value = switch (type.getSimpleName()) {
	                    case "Integer", "int" -> row.getInteger(name);
	                    case "String" -> row.getString(name);
                        case "double", "Double" -> row.getDouble(name);
	                    case "long", "Long" -> row.getLong(name);
	                    case "boolean", "Boolean" -> row.getBoolean(name);
	                    case "LocalDateTime" -> row.getLocalDateTime(name);
	                    case "BigDecimal" -> {
	                        try {
	                            var numeric = row.get(io.vertx.sqlclient.data.Numeric.class, row.getColumnIndex(name));
	                            yield numeric != null ? numeric.bigDecimalValue() : null;
	                        } catch (Exception e) {
	                            yield null;
	                        }
	                    }
	                    default -> {
                            Constants.LOGGER.error("Type not supported yet: {} for field {}", type.getName(), name);
	                        yield null;
	                    }
	                };

                }

                field.set(this, value);
            } catch (Exception e) {
                Constants.LOGGER.error("Error populating field {}: {}", field.getName(), e.getMessage());
            }
        }
    }

    /**
     * Codifica esta entidad como un objeto JSON, omitiendo los campos anotados con {@link APIDontReturn}.
     *
     * <p>Si un campo implementa {@link ValuableEnum}, se usará su valor en lugar del nombre del enum.</p>
     *
     * @return Representación JSON de esta entidad.
     */
    public String encode() {
        JsonObject json = new JsonObject();
        Class<?> clazz = this.getClass();

        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(APIDontReturn.class)) continue;

                field.setAccessible(true);
                try {
                    Object value = field.get(this);

                    if (value instanceof ValuableEnum ve) {
                        json.put(field.getName(), ve.getValue());
                    } else {
                        json.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    Constants.LOGGER.error("Error accessing field {}: {}", field.getName(), e.getMessage());
                }
            }
            clazz = clazz.getSuperclass();
        }

        return json.encode();
    }

    /**
     * Devuelve una representación en texto de la entidad, mostrando todos los campos y sus valores.
     *
     * <p>Útil para logs y debugging.</p>
     *
     * @return Cadena de texto con el nombre de la clase y todos los campos.
     */
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(this.getClass().getSimpleName()).append(" [ ");
    	Field[] fields = this.getClass().getDeclaredFields();
    	for (Field field : fields) {
			field.setAccessible(true);
			try {
				sb.append(field.getName()).append("= ").append(field.get(this)).append(", ");
			} catch (IllegalAccessException e) {
				Constants.LOGGER.error("Error stringing field {}: {}", field.getName(), e.getMessage());
            }
		}
		sb.append("]");
		return sb.toString();
    }

}
