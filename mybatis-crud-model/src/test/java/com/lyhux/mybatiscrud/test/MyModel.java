package com.lyhux.mybatiscrud.test;

import com.lyhux.mybatiscrud.model.GenericTypeResolver;
import com.lyhux.mybatiscrud.model.Model;

import java.lang.reflect.InvocationTargetException;

public class MyModel implements Model<MyBean> {

    public void print() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var type = GenericTypeResolver.resolveTypeArguments(getClass(), Model.class);
        System.out.printf("type: %s\n", type);
        var clazz = type[0];
        System.out.printf("class: %s\n", clazz.getDeclaredConstructor().newInstance());
    }

    @Override
    public void insert(MyBean myBean) {

    }
}
