package net.miarma.core.common.db;

import java.lang.reflect.Field;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import net.miarma.core.common.APIDontReturn;

public class AbstractEntity {
	
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
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(APIDontReturn.class)) continue;

            field.setAccessible(true);
            try {
                Object value = field.get(this);
                if (value instanceof Enum<?>) {
                    try {
                        var method = value.getClass().getMethod("getValue");
                        Object enumValue = method.invoke(value);
                        json.put(field.getName(), enumValue);
                    } catch (Exception e) {
                        json.put(field.getName(), ((Enum<?>) value).name());
                    }
                } else {
                    json.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return json.encode();
    }

}
