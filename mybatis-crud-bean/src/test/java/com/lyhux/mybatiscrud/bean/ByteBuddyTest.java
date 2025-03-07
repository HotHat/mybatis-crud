package com.lyhux.mybatiscrud.bean;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

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
            .method(named("load")).intercept(MethodDelegation.to(LoggerInterceptor.class))
            .make()
            .load(getClass().getClassLoader())
            .getLoaded()
            // .getDeclaredConstructor()
            .newInstance();

        System.out.printf("Logging interceptor loaded %s\n", loggingDatabase.load("333"));
    }

    @Test
    public void testAddField() throws InstantiationException, IllegalAccessException, NoSuchFieldException {
        /*
        DynamicType.Unloaded<Object> dynamicType = new ByteBuddy()
            .subclass(Object.class)
            .defineField("dynamicIntField", int.class, net.bytebuddy.description.modifier.Visibility.PUBLIC)
            .defineField("dynamicStringField", String.class, net.bytebuddy.description.modifier.Visibility.PUBLIC)
            .method(ElementMatchers.named("toString"))
            .intercept(FieldAccessor.ofField("dynamicStringField"))
            .make();

        Class<?> loadedClass = dynamicType.load(ByteBuddyAddField.class.getClassLoader()).getLoaded();
        Object instance = loadedClass.newInstance();

        Field intField = loadedClass.getField("dynamicIntField");
        intField.setInt(instance, 42);

        Field stringField = loadedClass.getField("dynamicStringField");
        stringField.set(instance, "Hello, Byte Buddy!");

        System.out.println("Dynamic Int Field: " + intField.getInt(instance));
        System.out.println("Dynamic String Field: " + stringField.get(instance));
        System.out.println("toString method output: " + instance.toString());

         */
    }
}
