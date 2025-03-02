package com.lyhux.sqlbuilder;


public class Utils {

    public static <T> T castToType(Object obj, Class<T> clazz) {
        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        }
        return null; // Or throw an exception if the cast is not possible
    }

}
