package com.lyhux.mybatiscrud.bean;

import com.lyhux.mybatiscrud.bean.annotation.TableColumn;
import com.lyhux.mybatiscrud.bean.annotation.TableKey;
import com.lyhux.mybatiscrud.bean.annotation.TableName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class BeanTest {
    @Test
    public void testBean() throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        MyBean bean = BeanFactory.getProxyBean(MyBean.class);
        Map<String, Object> map = new HashMap<>();
        map.put("name", "12344");
        map.put("age", 20);

        BeanFactory.setOriginalValue(bean, map);
        bean.setAge(25);
        bean.setName("test");

        System.out.printf("bean original value is : %s\n", BeanFactory.getOriginalValue(bean));


        MyBean bean1 = BeanFactory.getProxyBean(MyBean.class);
        MyBean bean2 = BeanFactory.getProxyBean(MyBean.class);
        MyBean bean3 = BeanFactory.getProxyBean(MyBean.class);

    }

    @Test
    public void testTableMetaInfo() {
        var info = BeanFactory.getMetaInfo(MyBean.class);

        System.out.printf("tableMetaInfo: %s, %s, %s, %s\n",
            info.getTableName(), info.getTableKey(), info.getKeyType(), info.getFieldColumnMap());
    }
}
