package com.lyhux.mybatiscrud.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;

public class TableMetaInfoTest {
    @Test
    public void testMetaInfo() {
        TableMetaInfo info = new TableMetaInfo("users");

        info.addFieldColumn("roleId", "role_id");
        info.addFieldColumn("roleName", "role_name");
        info.addFieldColumn("createdAt", "created_at");

    }

    @Test
    public void testTypeReflect() {
        TypeBean bean = new TypeBean();
        var fields = bean.getClass().getDeclaredFields();
        for (var field : fields) {
            var type = field.getType();
            System.out.printf("%s name:%s, simple name:%s\n", field.getName(), type.getName(), type.getSimpleName());
        }
    }

    @Test
    public void testBeanMap() throws Exception {
        // TypeBean bean = new TypeBean();

        /*
        int id;
        Integer age;
        String name;
        float price;
        Float salary;
        double amount;
        Date date;
         */
        var map = new HashMap<String, Object>();
        map.put("id", 10);
        map.put("age", Integer.valueOf(110));
        map.put("name", "lyhux");
        map.put("price", 10.99f);
        map.put("salary", Float.valueOf(10.00f));
        map.put("amount", "amount");
        map.put("date", Date.valueOf(LocalDate.now()));

        var bean = BeanMapUtil.mapToBean(map, TypeBean.class);

        Assertions.assertEquals(10, bean.getId());
        Assertions.assertEquals(0.00, bean.getAmount());
        Assertions.assertNotEquals(null, bean.getDate());
    }
}
