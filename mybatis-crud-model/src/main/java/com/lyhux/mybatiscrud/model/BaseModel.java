package com.lyhux.mybatiscrud.model;

import com.lyhux.mybatiscrud.bean.BeanFactory;

import java.util.Iterator;
import java.util.Map;

public class BaseModel<T> implements Model<T> {
    public void insert(T t) {
        var type = GenericTypeResolver.resolveTypeArguments(getClass(), Model.class);
        System.out.printf("Test type: %s\n", type);

        var info = BeanFactory.getMetaInfo(t.getClass());
        System.out.printf("table mate info: %s\n",  info);

        var manager = DatabaseManager.getInstance();
        var insertQuery = manager.insertQuery();

        insertQuery
            .table(info.getTableName())
            .values((wrapper) -> {
                for (Map.Entry<String, String> entry : info.getFieldColumnMap().entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    System.out.println("Key: " + key + ", Value: " + value);
                }
            })
        ;

    }
}
