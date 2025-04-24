package com.lyhux.mybatiscrud.model.test;


import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ModelTest {
    @Test
    public void modelTest() throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var model = new UserMapper();
        // MyBean bean = BeanFactory.getProxyBean(MyBean.class);
        UserBean bean = new UserBean();
        // bean.setId(1);
        bean.setUsername("model");
        bean.setPassword("password11");
        bean.setEmail("model@lyhux.com");
        bean.setGender(1);
        bean.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        bean.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        model.insert(bean);

        // System.out.printf("wrapper type %s\n", Wrapper.getGenericRuntimeType(new Wrapper<MyBean>(){}));
        // TypeReference<MyBean> doubleTypeReference = new TypeReference<MyBean>() {};
        // Class<MyBean> clazz = doubleTypeReference.getType();
        // System.out.println(clazz);

        // System.out.printf("||||||||||||||");
    }
}
