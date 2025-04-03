package net.miarma.api.common.db;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import net.miarma.api.common.ValuableEnum;
import net.miarma.api.common.annotations.APIDontReturn;

public abstract class AbstractEntity {
	
	public AbstractEntity() {}

    public AbstractEntity(Row row) {
        populateFromRow(row);
    }

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
                        case "Integer" -> row.getInteger(name);
                        case "String" -> row.getString(name);
                        case "Double" -> row.getDouble(name);
                        case "Long" -> row.getLong(name);
                        case "Boolean" -> row.getBoolean(name);
                        case "int" -> row.getInteger(name);
                        case "double" -> row.getDouble(name);
                        case "long" -> row.getLong(name);
                        case "boolean" -> row.getBoolean(name);
                        case "LocalDateTime" -> row.getLocalDateTime(name);
                        case "BigDecimal" -> row.get(BigDecimal.class, row.getColumnIndex(name));
                        default -> null;
                    };
                }

                field.set(this, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
                    e.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }

        return json.encode();
    }
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(this.getClass().getSimpleName()).append(" [ ");
    	Field[] fields = this.getClass().getDeclaredFields();
    	for (Field field : fields) {
			field.setAccessible(true);
			try {
				sb.append(field.getName()).append("= ").append(field.get(this)).append(", ");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		sb.append("]");
		return sb.toString();
    }

}
