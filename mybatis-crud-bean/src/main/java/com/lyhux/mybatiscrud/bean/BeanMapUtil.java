package com.lyhux.mybatiscrud.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class BeanMapUtil {

    /**
     * 对象转Map
     */
    public static Map<String, Object> beanToMap(Object object) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(object));
        }
        return map;
    }

    /**
     * map转对象
     */
    public static <T> T mapToBean(Map<String, ?> map, Class<T> beanClass) throws Exception {
        T object = beanClass.getDeclaredConstructor().newInstance();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            field.setAccessible(true);
            if (map.containsKey(field.getName())) {
                var value = map.get(field.getName());
                // type equal
                if (   (field.getType().getName().equals("int") && value instanceof Integer)
                    || (field.getType().getName().equals("long") && value instanceof Long)
                    || (field.getType().getName().equals("float") && value instanceof Float)
                    || (field.getType().getName().equals("double") && value instanceof Double)
                    || (field.getType().getName().equals("boolean") && value instanceof Boolean)
                    || (field.getType().getName().equals("byte") && value instanceof Byte)
                    || (field.getType().getName().equals("short") && value instanceof Short)
                    || (field.getType().getName().equals("char") && value instanceof Character)
                    || (field.getType().getName().equals(value.getClass().getName()) )
                ) {
                    field.set(object, map.get(field.getName()));
                }
            }
        }
        return object;
    }
}
