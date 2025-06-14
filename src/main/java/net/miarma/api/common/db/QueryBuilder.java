package net.miarma.api.common.db;

import net.miarma.api.common.Constants;
import net.miarma.api.common.annotations.Table;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Clase utilitaria para construir queries SQL dinámicamente mediante reflexión,
 * usando entidades anotadas con {@link Table}.
 * <p>
 * Soporta operaciones SELECT, INSERT, UPDATE (con y sin valores nulos), y UPSERT.
 * También permite aplicar filtros desde un mapa o directamente desde un objeto.
 * <p>
 * ¡Ojo! No ejecuta la query, solo la construye.
 *
 * @author José Manuel Amador Gallardo
 */
public class QueryBuilder {
    private final StringBuilder query;
    private String sort;
    private String order;
    private String limit;
    private Class<?> entityClass;

    public QueryBuilder() {
        this.query = new StringBuilder();
    }

    /**
     * Obtiene el nombre de la tabla desde la anotación @Table de la clase dada.
     */
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

    /**
     * Devuelve la consulta SQL construida hasta el momento.
     */
    public String getQuery() {
        return query.toString();
    }

    /**
     * Extrae el valor de un campo, manejando enums y devolviendo su valor o nombre.
     */
    private static Object extractValue(Object fieldValue) {
        if (fieldValue instanceof Enum<?>) {
            try {
                var method = fieldValue.getClass().getMethod("getValue");
                return method.invoke(fieldValue);
            } catch (Exception e) {
                return ((Enum<?>) fieldValue).name();
            }
        }
        return fieldValue;
    }

    /**
     * Escapa los caracteres especiales en una cadena para evitar inyecciones SQL.
     * @param value the string value to escape
     * @return the escaped string
     */
    private static String escapeSql(String value) {
        return value.replace("'", "''");
    }

    /**
     * Construye una consulta SELECT para la clase dada, con columnas opcionales.
     * @param clazz the entity class to query
     * @param columns optional columns to select; if empty, selects all columns
     * @return the current QueryBuilder instance
     * @param <T> the type of the entity class
     */
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

    /**
     * Añade una cláusula WHERE a la consulta actual, filtrando por los campos del mapa.
     * Los valores pueden ser números o cadenas, y se manejan adecuadamente.
     *
     * @param filters un mapa de filtros donde la clave es el nombre del campo y el valor es el valor a filtrar
     * @return el QueryBuilder actual para encadenar más métodos
     */
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

            if (!validFields.contains(key)) {
                Constants.LOGGER.warn("[QueryBuilder] Ignorando campo invalido en WHERE: {}", key);
                continue;
            }

            if (value.startsWith("(") && value.endsWith(")")) {
                conditions.add(key + " IN " + value);
            } else if (value.matches("-?\\d+(\\.\\d+)?")) {
                conditions.add(key + " = " + value);
            } else {
                conditions.add(key + " = '" + value + "'");
            }
        }

        if (!conditions.isEmpty()) {
            query.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
        }

        return this;
    }

    /**
     * Añade una cláusula WHERE a la consulta actual, filtrando por los campos del objeto.
     * Los valores se extraen mediante reflexión y se manejan adecuadamente.
     *
     * @param object el objeto del cual se extraerán los campos para filtrar
     * @return el QueryBuilder actual para encadenar más métodos
     */
    public <T> QueryBuilder where(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }

        Set<String> validFields = entityClass != null
            ? Arrays.stream(entityClass.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet())
            : Collections.emptySet();

        this.query.append("WHERE ");
        StringJoiner joiner = new StringJoiner(" AND ");
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(object);
                if (fieldValue != null) {
                    String key = field.getName();
                    if (!validFields.contains(key)) {
                        Constants.LOGGER.warn("[QueryBuilder] Ignorando campo invalido en WHERE: {}", key);
                        continue;
                    }
                    Object value = extractValue(fieldValue);
                    if (value instanceof String || value instanceof LocalDateTime) {
                        joiner.add(key + " = '" + value + "'");
                    } else {
                        joiner.add(key + " = " + value.toString());
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                Constants.LOGGER.error("(REFLECTION) Error reading field: {}", e.getMessage());
            }
        }
        this.query.append(joiner).append(" ");
        return this;
    }

    /**
     * Construye una consulta INSERT para el objeto dado, insertando todos sus campos.
     * Los valores se extraen mediante reflexión y se manejan adecuadamente.
     *
     * @param object el objeto a insertar
     * @return el QueryBuilder actual para encadenar más métodos
     * @param <T> el tipo del objeto a insertar
     */
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
                    if (value instanceof String || value instanceof LocalDateTime) {
                        values.add("'" + escapeSql((String) value) + "'");
                    } else {
                        values.add(value.toString());
                    }
                } else {
                    values.add("NULL");
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                Constants.LOGGER.error("(REFLECTION) Error reading field: {}", e.getMessage());
            }
        }
        qb.query.append(columns).append(") ");
        qb.query.append("VALUES (").append(values).append(") RETURNING * ");
        return qb;
    }

    /**
     * Construye una consulta UPDATE para el objeto dado, actualizando todos sus campos.
     * Los valores se extraen mediante reflexión y se manejan adecuadamente.
     * Requiere que el objeto tenga un campo ID (terminado en _id) para la cláusula WHERE.
     *
     * @param object el objeto a actualizar
     * @return el QueryBuilder actual para encadenar más métodos
     * @param <T> el tipo del objeto a actualizar
     */
    public static <T> QueryBuilder update(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }

        QueryBuilder qb = new QueryBuilder();
        String table = getTableName(object.getClass());
        qb.query.append("UPDATE ").append(table).append(" SET ");

        StringJoiner setJoiner = new StringJoiner(", ");
        StringJoiner whereJoiner = new StringJoiner(" AND ");

        Field idField = null;

        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(object);
                if (fieldValue == null) continue;

                String fieldName = field.getName();
                Object value = extractValue(fieldValue);

                if (fieldName.endsWith("_id")) {
                    idField = field;
                    whereJoiner.add(fieldName + " = " + (value instanceof String
                    		|| value instanceof LocalDateTime ? "'" + value + "'" : value));
                    continue;
                }

                setJoiner.add(fieldName + " = " + (value instanceof String
                		|| value instanceof LocalDateTime ? "'" + value + "'" : value));
            } catch (Exception e) {
                Constants.LOGGER.error("(REFLECTION) Error reading field: {}", e.getMessage());
            }
        }

        if (idField == null) {
            throw new IllegalArgumentException("No ID field (ending with _id) found for WHERE clause");
        }

        qb.query.append(setJoiner).append(" WHERE ").append(whereJoiner);
        return qb;
    }

    /**
     * Construye una consulta UPDATE que establece los campos a NULL si son nulos.
     * Requiere que el objeto tenga un campo ID (terminado en _id) para la cláusula WHERE.
     *
     * @param object el objeto a actualizar
     * @return el QueryBuilder actual para encadenar más métodos
     * @param <T> el tipo del objeto a actualizar
     */
    public static <T> QueryBuilder updateWithNulls(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }

        QueryBuilder qb = new QueryBuilder();
        String table = getTableName(object.getClass());
        qb.query.append("UPDATE ").append(table).append(" SET ");

        StringJoiner setJoiner = new StringJoiner(", ");
        StringJoiner whereJoiner = new StringJoiner(" AND ");

        Field idField = null;

        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                String fieldName = field.getName();
                Object fieldValue = field.get(object);

                if (fieldName.endsWith("_id")) {
                    idField = field;
                    Object value = extractValue(fieldValue);
                    whereJoiner.add(fieldName + " = " + (value instanceof String || value instanceof LocalDateTime ? "'" + value + "'" : value));
                    continue;
                }

                if (fieldValue == null) {
                    setJoiner.add(fieldName + " = NULL");
                } else {
                    Object value = extractValue(fieldValue);
                    setJoiner.add(fieldName + " = " + (value instanceof String || value instanceof LocalDateTime ? "'" + value + "'" : value));
                }
            } catch (Exception e) {
                Constants.LOGGER.error("(REFLECTION) Error reading field: {}", e.getMessage());
            }
        }

        if (idField == null) {
            throw new IllegalArgumentException("No ID field (ending with _id) found for WHERE clause");
        }

        qb.query.append(setJoiner).append(" WHERE ").append(whereJoiner);
        return qb;
    }

    /**
     * Construye una consulta UPSERT (INSERT o UPDATE) para el objeto dado.
     * Si hay claves de conflicto, se actualizan los campos excepto las claves duplicadas.
     *
     * @param object el objeto a insertar o actualizar
     * @param conflictKeys las claves que causan conflictos y no deben actualizarse
     * @return el QueryBuilder actual para encadenar más métodos
     * @param <T> el tipo del objeto a insertar o actualizar
     */
    public static <T> QueryBuilder upsert(T object, String... conflictKeys) {
        if (object == null) throw new IllegalArgumentException("Object cannot be null");

        QueryBuilder qb = new QueryBuilder();
        String table = getTableName(object.getClass());
        qb.query.append("INSERT INTO ").append(table).append(" ");

        StringJoiner columns = new StringJoiner(", ");
        StringJoiner values = new StringJoiner(", ");
        Map<String, String> updates = new HashMap<>();

        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(object);
                String columnName = field.getName();
                columns.add(columnName);

                Object value = extractValue(fieldValue);
                String valueStr = value == null ? "NULL"
                        : (value instanceof String || value instanceof LocalDateTime ? "'" + value + "'" : value.toString());
                values.add(valueStr);

                // no actualizamos la clave duplicada
                boolean isConflictKey = Arrays.asList(conflictKeys).contains(columnName);
                if (!isConflictKey) {
                    updates.put(columnName, valueStr);
                }

            } catch (Exception e) {
                Constants.LOGGER.error("(REFLECTION) Error reading field: {}", e.getMessage());
            }
        }

        qb.query.append("(").append(columns).append(") VALUES (").append(values).append(")");

        if (conflictKeys.length > 0 && !updates.isEmpty()) {
            qb.query.append(" ON DUPLICATE KEY UPDATE ");
            StringJoiner updateSet = new StringJoiner(", ");
            updates.forEach((k, v) -> updateSet.add(k + " = " + v));
            qb.query.append(updateSet);
        }

        return qb;
    }

    /**
     * Construye una consulta DELETE para el objeto dado, eliminando registros que coincidan con sus campos.
     * Los valores se extraen mediante reflexión y se manejan adecuadamente.
     *
     * @param object el objeto a eliminar
     * @return el QueryBuilder actual para encadenar más métodos
     * @param <T> el tipo del objeto a eliminar
     */
    public static <T> QueryBuilder delete(T object) {
        if (object == null) throw new IllegalArgumentException("Object cannot be null");

        QueryBuilder qb = new QueryBuilder();
        String table = getTableName(object.getClass());
        qb.query.append("DELETE FROM ").append(table).append(" WHERE ");

        StringJoiner joiner = new StringJoiner(" AND ");
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(object);
                if (fieldValue != null) {
                    Object value = extractValue(fieldValue);
                    joiner.add(field.getName() + " = " + (value instanceof String
                    		|| value instanceof LocalDateTime ? "'" + value + "'" : value.toString()));
                }
            } catch (Exception e) {
                Constants.LOGGER.error("(REFLECTION) Error reading field: {}", e.getMessage());
            }
        }

        qb.query.append(joiner).append(" ");
        return qb;
    }

    /**
     * Añade una cláusula ORDER BY a la consulta actual, ordenando por la columna y el orden especificados.
     * Si la columna no es válida, se ignora.
     *
     * @param column la columna por la que ordenar
     * @param order  el orden (ASC o DESC); si no se especifica, se asume ASC
     * @return el QueryBuilder actual para encadenar más métodos
     */
    public QueryBuilder orderBy(Optional<String> column, Optional<String> order) {
        column.ifPresent(c -> {
            if (entityClass != null) {
                boolean isValid = Arrays.stream(entityClass.getDeclaredFields())
                    .map(Field::getName)
                    .anyMatch(f -> f.equals(c));

                if (!isValid) {
                    Constants.LOGGER.warn("[QueryBuilder] Ignorando campo invalido en ORDER BY: {}", c);
                    return;
                }
            }

            sort = "ORDER BY " + c + " ";
            order.ifPresent(o -> sort += o.equalsIgnoreCase("asc") ? "ASC" : "DESC" + " ");
        });
        return this;
    }

    /**
     * Añade una cláusula LIMIT a la consulta actual, limitando el número de resultados.
     * Si se especifica un offset, se añade también.
     *
     * @param limitParam el número máximo de resultados a devolver; si no se especifica, no se aplica límite
     * @return el QueryBuilder actual para encadenar más métodos
     */
    public QueryBuilder limit(Optional<Integer> limitParam) {
        limitParam.ifPresent(param -> limit = "LIMIT " + param + " ");
        return this;
    }

    /**
     * Añade una cláusula OFFSET a la consulta actual, desplazando el inicio de los resultados.
     * Si se especifica un offset, se añade también.
     *
     * @param offsetParam el número de resultados a omitir antes de empezar a devolver resultados; si no se especifica, no se aplica offset
     * @return el QueryBuilder actual para encadenar más métodos
     */
    public QueryBuilder offset(Optional<Integer> offsetParam) {
        offsetParam.ifPresent(param -> limit += "OFFSET " + param + " ");
        return this;
    }

    /**
     * Construye y devuelve la consulta SQL completa.
     * Si no se han añadido cláusulas ORDER BY, LIMIT o OFFSET, las omite.
     *
     * @return la consulta SQL construida
     */
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