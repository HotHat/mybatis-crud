package com.lyhux.mybatiscrud.model;

import com.lyhux.mybatiscrud.bean.BeanFactory;
import com.lyhux.mybatiscrud.bean.annotation.KeyType;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class BaseModel<T> implements Model<T> {
    public void insert(T bean) {
        // var type = GenericTypeResolver.resolveTypeArguments(getClass(), Model.class);

        var info = BeanFactory.getMetaInfo(bean.getClass());
        // System.out.printf("table mate info: %s\n",  info);

        var manager = DatabaseManager.getInstance();
        var insertQuery = manager.insertQuery();

        // get columns
        ArrayList<String> columns = new ArrayList<>();

        try {
            for (Map.Entry<String, String> entry : info.getFieldColumnMap().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                // primary key
                if (info.getTableKey().equals(key)) {
                    if (info.isPrimaryKeyInteger() || info.isPrimaryKeyLong()) {
                        Method getter = new PropertyDescriptor(key, bean.getClass()).getReadMethod();
                        // integer or long primary key set value
                        if (info.isPrimaryKeyInteger()) {
                            Integer val = (Integer) getter.invoke(bean);
                            if (val != null && !val.equals(0)) {
                                columns.add(value);
                            }
                        } else {
                            Long val = (Long) getter.invoke(bean);
                            if (val != null && !val.equals(0L)) {
                                columns.add(value);
                            }
                        }

                    }
                } else {
                    columns.add(value);
                }
            }

            var query = insertQuery
                .table(info.getTableName())
                .columns(columns.toArray(new String[0]))
                .values((wrapper) -> {
                    try {
                        for (Map.Entry<String, String> entry : info.getFieldColumnMap().entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            if (columns.contains(value)) {
                                Method getter = new PropertyDescriptor(key, bean.getClass()).getReadMethod();
                                // Class<?> type = getter.getReturnType();
                                Object val = getter.invoke(bean);
                                wrapper.add(val);
                            }
                        }
                    } catch (Exception  e) {
                        throw new RuntimeException(e);
                    }
                });

            if ((info.isPrimaryKeyInteger() || info.isPrimaryKeyLong()) && info.getKeyType() == KeyType.AUTO) {
                Long primaryKey = query.insertGetId();

                // update auto increment primary key
                Method setter = new PropertyDescriptor(info.getTableKey(), bean.getClass()).getWriteMethod();
                if (info.isPrimaryKeyInteger()) {
                    setter.invoke(bean, Math.toIntExact(primaryKey));
                } else {
                    setter.invoke(bean, primaryKey);
                }
            } else {
                query.insert();
            }

        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException | SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
