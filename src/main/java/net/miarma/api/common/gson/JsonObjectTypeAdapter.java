package net.miarma.api.common.gson;

import com.google.gson.*;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Adaptador de tipo para Gson que maneja la serialización y deserialización de objetos JsonObject.
 * Este adaptador asegura que los objetos JsonObject se serialicen correctamente sin incluir el mapa interno.
 *
 * @author José Manuel Amador Gallardo
 */
public class JsonObjectTypeAdapter implements JsonSerializer<JsonObject>, JsonDeserializer<JsonObject> {

    @Override
    public JsonElement serialize(JsonObject src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject safe = src == null ? new JsonObject() : src;
        JsonObject wrapped = new JsonObject(safe.getMap()); // evita el map dentro
        return context.serialize(wrapped.getMap());
    }

    @Override
    public JsonObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("Expected JsonObject");
        }

        JsonObject obj = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            obj.put(entry.getKey(), context.deserialize(entry.getValue(), Object.class));
        }

        return obj;
    }
}
