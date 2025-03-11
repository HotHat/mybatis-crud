package com.lyhux.mybatiscrud.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Wrapper<T> {
    public static <T> Type getGenericRuntimeType(Wrapper<T> wrapper) {
        Type type = wrapper.getClass().getGenericSuperclass();
        if (type == null) {
            return null;
        }

        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType)type).getActualTypeArguments();
            return types[0];
        }
        return null;
    }
    public static Class<?>[] resolveTypeArguments(final Class<?> clazz, final Class<?> genericType) {
        Type[] type = clazz.getGenericInterfaces();

        return null;
    }
}
