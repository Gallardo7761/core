package net.miarma.api.common.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import net.miarma.api.common.annotations.APIDontReturn;

/**
 * Estrategia de exclusión para Gson que omite campos anotados con @APIDontReturn.
 * Esta estrategia se utiliza para evitar que ciertos campos sean serializados
 * y enviados en las respuestas de la API.
 *
 * @author José Manuel Amador Gallardo
 */
public class APIDontReturnExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(APIDontReturn.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
