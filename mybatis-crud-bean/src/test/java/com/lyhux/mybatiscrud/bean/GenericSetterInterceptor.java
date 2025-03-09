package com.lyhux.mybatiscrud.bean;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;


public class GenericSetterInterceptor {

    public void intercept(@This Object bean, @Origin Method method, @AllArguments Object[] args, @SuperCall Callable<?> superCall) throws Exception {
        System.out.println("Intercepted setter: " + method.getName() + " with value: " + args[0]);
        // System.out.printf("Intercepted counter: %s\n", counter++);

        // Field intField = bean.getClass().getField("counter");
        // Map<String, Object> original = (Map<String, Object>) intField.get(bean);
        // original.put(method.getName(), args[0]);

        superCall.call();
        // method.invoke(bean, ); // Invoke the original setter.
    }
}