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
                Object value = switch (field.getType().getSimpleName()) {
                    case "Integer" -> row.getInteger(field.getName());
                    case "String" -> row.getString(field.getName());
                    case "Double" -> row.getDouble(field.getName());
                    case "Long" -> row.getLong(field.getName());
                    case "Boolean" -> row.getBoolean(field.getName());
                    case "int" -> row.getInteger(field.getName());
                    case "double" -> row.getDouble(field.getName());
                    case "long" -> row.getLong(field.getName());
                    case "boolean" -> row.getBoolean(field.getName());
                    default -> null;
                };
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
                json.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return json.encode();
    }
}
