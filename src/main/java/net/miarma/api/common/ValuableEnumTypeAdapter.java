package net.miarma.api.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ValuableEnumTypeAdapter implements JsonSerializer<ValuableEnum> {

    @Override
    public JsonElement serialize(ValuableEnum src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getValue());
    }
}
