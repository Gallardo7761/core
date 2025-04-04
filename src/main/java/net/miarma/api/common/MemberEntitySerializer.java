package net.miarma.api.common;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.miarma.api.huertos.entities.MemberEntity;

public class MemberEntitySerializer implements JsonSerializer<MemberEntity> {
    @Override
    public JsonElement serialize(MemberEntity member, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();

        json.add("user", context.serialize(member.getUser()));
        json.add("metadata", context.serialize(member.getMetadata()));

        return json;
    }
}
