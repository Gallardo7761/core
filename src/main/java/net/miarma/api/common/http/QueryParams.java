package net.miarma.api.common.http;

import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representa los parámetros de consulta para una solicitud HTTP.
 * Esta clase encapsula los filtros de consulta y los filtros adicionales
 * que se pueden aplicar a una consulta.
 *
 * @author José Manuel Amador Gallardo
 */
public class QueryParams {

    private final Map<String, String> filters;
    private final QueryFilters queryFilters;

    public QueryParams(Map<String, String> filters, QueryFilters queryFilters) {
        this.filters = filters;
        this.queryFilters = queryFilters;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public QueryFilters getQueryFilters() {
        return queryFilters;
    }

    public static QueryParams from(RoutingContext ctx) {
        Map<String, String> filters = new HashMap<>();

        QueryFilters queryFilters = QueryFilters.from(ctx);

        ctx.queryParams().forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();

            if (!key.startsWith("_")) { // esto es un filtro válido
                filters.put(key, value);
            }
        });

        return new QueryParams(filters, queryFilters);
    }
    
    public static QueryParams filterForEntity(QueryParams original, Class<?> entityClass, String prefix) {
        Set<String> validKeys = getFieldNames(entityClass);

        Map<String, String> filtered = original.getFilters().entrySet().stream()
            .filter(e -> {
                String key = e.getKey();
                return key.startsWith(prefix + ".") && validKeys.contains(key.substring(prefix.length() + 1));
            })
            .collect(Collectors.toMap(
                e -> e.getKey().substring(prefix.length() + 1), // quitar el prefijo
                Map.Entry::getValue
            ));

        return new QueryParams(filtered, original.getQueryFilters());
    }


    
    private static Set<String> getFieldNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
            .map(Field::getName)
            .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "QueryParams{" +
                "filters=" + filters +
                ", queryFilters=" + queryFilters +
                '}';
    }
}
