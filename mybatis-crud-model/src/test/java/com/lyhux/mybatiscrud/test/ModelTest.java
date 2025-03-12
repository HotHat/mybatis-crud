package com.lyhux.mybatiscrud.test;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

public class ModelTest {
    @Test
    public void modelTest() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var model = new MyModel();
        model.print();

        // System.out.printf("wrapper type %s\n", Wrapper.getGenericRuntimeType(new Wrapper<MyBean>(){}));
        // TypeReference<MyBean> doubleTypeReference = new TypeReference<MyBean>() {};
        // Class<MyBean> clazz = doubleTypeReference.getType();
        // System.out.println(clazz);

        // System.out.printf("||||||||||||||");
    }
}
