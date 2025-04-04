package net.miarma.api.common.gson;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import io.vertx.core.json.JsonObject;

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
