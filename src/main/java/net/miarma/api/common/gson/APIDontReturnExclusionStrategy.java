package net.miarma.api.common.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import net.miarma.api.common.annotations.APIDontReturn;

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
