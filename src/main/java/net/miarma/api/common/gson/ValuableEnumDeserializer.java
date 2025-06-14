package net.miarma.api.common.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.miarma.api.common.ValuableEnum;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Deserializador de Gson para enumeraciones que implementan ValuableEnum.
 * Este deserializador convierte un valor entero en una instancia de la enumeración correspondiente.
 *
 * @author José Manuel Amador Gallardo
 */
public class ValuableEnumDeserializer implements JsonDeserializer<ValuableEnum> {

    @Override
    public ValuableEnum deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Class<?> enumClass = (Class<?>) typeOfT;
        int value = json.getAsInt();

        return (ValuableEnum) Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> ((ValuableEnum) e).getValue() == value)
                .findFirst()
                .orElseThrow(() -> new JsonParseException("Invalid enum value: " + value));
    }
}
