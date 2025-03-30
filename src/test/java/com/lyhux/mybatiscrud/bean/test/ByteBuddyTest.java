package com.lyhux.mybatiscrud.bean.test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.named;


public class ByteBuddyTest {
    @Test
    public void testBuddy1() {
        System.out.printf("Testing ByteBuddy1\n");
    }

    @Test
    public void testLoggingInterceptor() throws InstantiationException, IllegalAccessException {
        /*
        MemoryDatabase  db = new ByteBuddy()
            .subclass(MemoryDatabase.class)
            .name("example.Type")
            .make()
            .load(getClass().getClassLoader())
            .getLoaded()
            .newInstance()
            ;
        System.out.printf("Testing ByteBuddy2 %s\n", db.load("2222"));
         */
        MemoryDatabase loggingDatabase = new ByteBuddy()
            .subclass(MemoryDatabase.class)
            .method(ElementMatchers.named("load")).intercept(MethodDelegation.to(LoggerInterceptor.class))
            .make()
            .load(getClass().getClassLoader())
            .getLoaded()
            // .getDeclaredConstructor()
            .newInstance();

        System.out.printf("Logging interceptor loaded %s\n", loggingDatabase.load("333"));
    }

    @Test
    public void testAddField() throws InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        DynamicType.Unloaded<Object> dynamicType = new ByteBuddy()
            .subclass(Object.class)
            .defineField("dynamicIntField", int.class, net.bytebuddy.description.modifier.Visibility.PUBLIC)
            .defineField("dynamicStringField", String.class, net.bytebuddy.description.modifier.Visibility.PUBLIC)
            .method(ElementMatchers.named("toString"))
            .intercept(FieldAccessor.ofField("dynamicStringField"))
            .make();

        Class<?> loadedClass = dynamicType.load(ByteBuddyTest.class.getClassLoader()).getLoaded();
        Object instance = loadedClass.getDeclaredConstructor().newInstance();

        Field intField = loadedClass.getField("dynamicIntField");
        intField.setInt(instance, 42);

        Field stringField = loadedClass.getField("dynamicStringField");
        stringField.set(instance, "Hello, Byte Buddy!");

        System.out.println("Dynamic Int Field: " + intField.getInt(instance));
        System.out.println("Dynamic String Field: " + stringField.get(instance));
        System.out.println("toString method output: " + instance.toString());
    }

    public static <T> Class<? extends T> addSetterInterceptor(Class<T> beanClass, Object interceptor) {
        return new ByteBuddy()
            .subclass(beanClass)
            .method(ElementMatchers.isSetter())
            .intercept(MethodDelegation.to(interceptor))
            .make()
            .load(beanClass.getClassLoader())
            .getLoaded();
    }

    @Test
    public void testSetter() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        GenericSetterInterceptor interceptor = new GenericSetterInterceptor();
        Class<? extends MyBean> beanClassWithInterceptor = addSetterInterceptor(MyBean.class, interceptor);
        MyBean bean = beanClassWithInterceptor.getDeclaredConstructor().newInstance();

        bean.setName("Alice");
        bean.setAge(28);

        System.out.println("Name: " + bean.getName());
        System.out.println("Age: " + bean.getAge());

        // GenericSetterInterceptor interceptor2 = new GenericSetterInterceptor();
        // Class<? extends MyBean> beanClassWithInterceptor2 = addSetterInterceptor(MyBean.class, interceptor2);
        // MyBean bean2 = beanClassWithInterceptor2.getDeclaredConstructor().newInstance();
        //
        // bean2.setName("Alice2");
        // bean2.setAge(38);
        //
        // System.out.println("Name: " + bean2.getName());
        // System.out.println("Age: " + bean2.getAge());
    }
    @Test
    public void testAddFieldAddSetter() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        GenericSetterInterceptor interceptor = new GenericSetterInterceptor();
        Class<? extends MyBean> beanClassWithInterceptor = addSetterInterceptor(MyBean.class, interceptor);

        Class<? extends MyBean> dynamicType = new ByteBuddy()
            .subclass(MyBean.class)
            .defineField("keymap$original", Map.class, net.bytebuddy.description.modifier.Visibility.PUBLIC)
            // .implement(Map.class)
            // .intercept(FieldAccessor.ofField("keymap$original"))
            .method(ElementMatchers.isSetter())
            .intercept(MethodDelegation.to(interceptor))
            .make()
            .load(MyBean.class.getClassLoader())
            .getLoaded();

        MyBean instance = dynamicType.getDeclaredConstructor().newInstance();

        Field mapField = dynamicType.getDeclaredField("keymap$original");
        mapField.setAccessible(true);
        Map<String, Object> map1;
        map1 = new HashMap<>();
        map1.put("key1", "value1");
        map1.put("key2", 123);
        mapField.set(instance, map1);

        instance.setName("Alice");
        instance.setAge(28);
        System.out.println("Name: " + instance.getName());
        System.out.println("Age: " + instance.getAge());

        System.out.println("keymap: " + instance.getClass().getField("keymap$original").get(instance));

    }
}
