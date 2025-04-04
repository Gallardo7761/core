package net.miarma.api.common.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import net.miarma.api.common.Constants;
import net.miarma.api.common.annotations.Table;

public class QueryBuilder {
	private StringBuilder query;
    private String sort;
    private String order;
    private String limit;
    private Class<?> entityClass;

    public QueryBuilder() {
        this.query = new StringBuilder();
    }

    private static <T> String getTableName(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        if (clazz.isAnnotationPresent(Table.class)) {
            Table annotation = clazz.getAnnotation(Table.class);
            return annotation.value();
        }
        throw new IllegalArgumentException("Class does not have @Table annotation");
    }

    public String getQuery() {
        return query.toString();
    }

    private static Object extractValue(Object fieldValue) {
        if (fieldValue instanceof Enum<?>) {
            try {
                var method = fieldValue.getClass().getMethod("getValue");
                return method.invoke(fieldValue);
            } catch (Exception e) {
                return ((Enum<?>) fieldValue).name(); // fallback
            }
        }
        return fieldValue;
    }

    public static <T> QueryBuilder select(Class<T> clazz, String... columns) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        QueryBuilder qb = new QueryBuilder();
        qb.entityClass = clazz;
        String tableName = getTableName(clazz);

        qb.query.append("SELECT ");

        if (columns.length == 0) {
            qb.query.append("* ");
        } else {
            StringJoiner joiner = new StringJoiner(", ");
            for (String column : columns) {
                if (column != null) {
                    joiner.add(column);
                }
            }
            qb.query.append(joiner).append(" ");
        }

        qb.query.append("FROM ").append(tableName).append(" ");
        return qb;
    }

    public QueryBuilder where(Map<String, String> filters) {
        if (filters == null || filters.isEmpty()) {
            return this;
        }

        Set<String> validFields = entityClass != null
            ? Arrays.stream(entityClass.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet())
            : Collections.emptySet();

        List<String> conditions = new ArrayList<>();

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            String actualKey = key.contains(".") ? key.substring(key.indexOf('.') + 1) : key;

            if (!validFields.contains(actualKey)) {
                Constants.LOGGER.warn("[QueryBuilder] Ignorando campo invalido en WHERE: " + actualKey);
                continue;
            }

            if (value.startsWith("(") && value.endsWith(")")) {
                conditions.add(actualKey + " IN " + value);
            } else if (value.matches("-?\\d+(\\.\\d+)?")) {
                conditions.add(actualKey + " = " + value);
            } else {
                conditions.add(actualKey + " = '" + value + "'");
            }
        }

        if (!conditions.isEmpty()) {
            query.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
        }

        return this;
    }

    public QueryBuilder whereWithAlias(Map<String, String> filters, String alias) {
        if (filters == null || filters.isEmpty()) {
            return this;
        }

        Set<String> validFields = entityClass != null
            ? Arrays.stream(entityClass.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet())
            : Collections.emptySet();

        List<String> conditions = new ArrayList<>();

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            String actualKey = key.contains(".") ? key.substring(key.indexOf('.') + 1) : key;

            if (!validFields.contains(actualKey)) {
                Constants.LOGGER.warn("[QueryBuilder] Ignorando campo invalido en WHERE (alias): " + actualKey);
                continue;
            }

            if (value.startsWith("(") && value.endsWith(")")) {
                conditions.add(alias + "." + actualKey + " IN " + value);
            } else if (value.matches("-?\\d+(\\.\\d+)?")) {
                conditions.add(alias + "." + actualKey + " = " + value);
            } else {
                conditions.add(alias + "." + actualKey + " = '" + value + "'");
            }
        }

        if (!conditions.isEmpty()) {
            if (!query.toString().contains("WHERE")) {
                query.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
            } else {
                query.append("AND ").append(String.join(" AND ", conditions)).append(" ");
            }
        }

        return this;
    }

    public static <T> QueryBuilder insert(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }

        QueryBuilder qb = new QueryBuilder();
        String table = getTableName(object.getClass());
        qb.query.append("INSERT INTO ").append(table).append(" ");
        qb.query.append("(");
        StringJoiner columns = new StringJoiner(", ");
        StringJoiner values = new StringJoiner(", ");
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                columns.add(field.getName());
                Object fieldValue = field.get(object);
                if (fieldValue != null) {
                    Object value = extractValue(fieldValue);
                    if (value instanceof String) {
                        values.add("'" + value + "'");
                    } else {
                        values.add(value.toString());
                    }
                } else {
                    values.add("NULL");
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                Constants.LOGGER.error("(REFLECTION) Error reading field: " + e.getMessage());
            }
        }
        qb.query.append(columns).append(") ");
        qb.query.append("VALUES (").append(values).append(") ");
        return qb;
    }

    public static <T> QueryBuilder update(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }

        QueryBuilder qb = new QueryBuilder();
        String table = getTableName(object.getClass());
        qb.query.append("UPDATE ").append(table).append(" ");
        qb.query.append("SET ");
        StringJoiner joiner = new StringJoiner(", ");
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(object);
                if (fieldValue != null) {
                    Object value = extractValue(fieldValue);
                    if (value instanceof String) {
                        joiner.add(field.getName() + " = '" + value + "'");
                    } else {
                        joiner.add(field.getName() + " = " + value.toString());
                    }
                } else {
                    joiner.add(field.getName() + " = NULL");
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                Constants.LOGGER.error("(REFLECTION) Error reading field: " + e.getMessage());
            }
        }
        qb.query.append(joiner).append(" ");
        return qb;
    }

    public static <T> QueryBuilder delete(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }

        QueryBuilder qb = new QueryBuilder();
        String table = getTableName(object.getClass());
        qb.query.append("DELETE FROM ").append(table).append(" ");
        qb.query.append("WHERE ");
        StringJoiner joiner = new StringJoiner(" AND ");
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(object);
                if (fieldValue != null) {
                    Object value = extractValue(fieldValue);
                    if (value instanceof String) {
                        joiner.add(field.getName() + " = '" + value + "'");
                    } else {
                        joiner.add(field.getName() + " = " + value.toString());
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                Constants.LOGGER.error("(REFLECTION) Error reading field: " + e.getMessage());
            }
        }
        qb.query.append(joiner).append(" ");
        return qb;
    }

    public QueryBuilder orderBy(Optional<String> column, Optional<String> order) {
        column.ifPresent(c -> {
            String field = c.contains(".") ? c.substring(c.indexOf('.') + 1) : c;

            if (entityClass != null) {
                boolean isValid = Arrays.stream(entityClass.getDeclaredFields())
                    .map(Field::getName)
                    .anyMatch(f -> f.equals(field));

                if (!isValid) {
                    Constants.LOGGER.warn("[QueryBuilder] Ignorando campo invalido en ORDER BY: " + field);
                    return;
                }
            }

            sort = "ORDER BY " + field + " ";
            order.ifPresent(o -> {
                sort += o.equalsIgnoreCase("asc") ? "ASC" : "DESC" + " ";
            });
        });
        return this;
    }

    public QueryBuilder limit(Optional<Integer> limitParam) {
        limitParam.ifPresent(param -> limit = "LIMIT " + param + " ");
        return this;
    }

    public QueryBuilder offset(Optional<Integer> offsetParam) {
        offsetParam.ifPresent(param -> limit += "OFFSET " + param + " ");
        return this;
    }

    public String build() {
        if (order != null && !order.isEmpty()) {
            query.append(order);
        }
        if (sort != null && !sort.isEmpty()) {
            query.append(sort);
        }
        if (limit != null && !limit.isEmpty()) {
            query.append(limit);
        }
        return query.toString().trim() + ";";
    }
}
