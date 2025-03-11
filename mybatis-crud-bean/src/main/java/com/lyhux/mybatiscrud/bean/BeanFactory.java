package com.lyhux.mybatiscrud.bean;

import com.lyhux.mybatiscrud.bean.annotation.KeyType;
import com.lyhux.mybatiscrud.bean.annotation.TableColumn;
import com.lyhux.mybatiscrud.bean.annotation.TableKey;
import com.lyhux.mybatiscrud.bean.annotation.TableName;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory {
    final static String originalValueKey = "$meta$info";
    final static ByteBuddyCache byteBuddyCache = new ByteBuddyCache();

    public static class ByteBuddyCache {

        private final Map<String, Class<?>> typeCache = new ConcurrentHashMap<>();

        public <T> Class<?> generateCachedType(Class<T> clazz, String typeKey) {
            return typeCache.computeIfAbsent(typeKey, key -> createDynamicType(clazz, typeKey));
        }

        // Example Usage
        public <T> Class<? extends T> createDynamicType(Class<T> clazz, String className){
            Class<? extends T> dynamicType = new ByteBuddy()
                .subclass(clazz)
                .name(className)
                .defineField(originalValueKey, Map.class, Visibility.PRIVATE)
                .make()
                .load(clazz.getClassLoader())
                .getLoaded();

            return dynamicType;
        }
    }

    public static<T> T getProxyBean(Class<T> clazz)
        throws NoSuchMethodException, NoSuchFieldException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        var clazzProxyName = clazz.getCanonicalName() + "$Proxy";

       return (T)byteBuddyCache.generateCachedType(clazz, clazzProxyName).getDeclaredConstructor().newInstance();
    }

    public static<T> void setOriginalValue(T bean, Map<String, Object> originalValue)
        throws NoSuchFieldException, IllegalAccessException {
        Field field = bean.getClass().getDeclaredField(originalValueKey);
        field.setAccessible(true);
        field.set(bean, originalValue);
        field.setAccessible(false);
    }

    public static<T> Map<String, Object> getOriginalValue(T bean) {
        try {
            Field field = bean.getClass().getDeclaredField(originalValueKey);
            field.setAccessible(true);
            var result = (Map<String, Object>) field.get(bean);
            field.setAccessible(false);
            return result;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    public static<T> TableMetaInfo getMetaInfo(Class<T> clazz) {
        var tableName = clazz.getSimpleName();

        var isTaleName = clazz.isAnnotationPresent(TableName.class);
        if (isTaleName) {
            TableName tna = clazz.getAnnotation(TableName.class);
            tableName = tna.value();
        }

        var fields = clazz.getDeclaredFields();

        var primaryKey = "";
        var keyType = KeyType.NONE;
        var filedMap = new HashMap<String, String>();

        for (var field : fields) {
            var key = field.getName();
            var value = key;
            var isTCA = field.isAnnotationPresent(TableColumn.class);
            if (isTCA) {
            }

            var isTKA = field.isAnnotationPresent(TableKey.class);
            if (isTKA) {
                TableKey tka = field.getAnnotation(TableKey.class);
                primaryKey = key;
                keyType = tka.type();
            } else if (field.isAnnotationPresent(TableColumn.class)) {
                TableColumn tca = field.getAnnotation(TableColumn.class);
                value = tca.value();
            }

            filedMap.put(key, value);
        }

        return new TableMetaInfo(tableName, primaryKey, keyType, filedMap);
    }


}
