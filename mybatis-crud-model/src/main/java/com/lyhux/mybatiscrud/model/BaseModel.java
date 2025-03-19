package com.lyhux.mybatiscrud.model;

import com.lyhux.mybatiscrud.bean.BeanFactory;
import com.lyhux.mybatiscrud.bean.BeanMapUtil;
import com.lyhux.mybatiscrud.bean.annotation.KeyType;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;

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

    public void update(T bean) {
        var metaInfo = BeanFactory.getMetaInfo(getBeanType());

        var original = BeanFactory.getOriginalValue(bean);

        Map<String, Object> updateData = new HashMap<>();


        var manager = DatabaseManager.getInstance();
        var updateQuery = manager.updateQuery();


        var query = updateQuery
            .table(metaInfo.getTableName());

        try {
            if (original != null) {
                for (Map.Entry<String, String> entry : metaInfo.getFieldColumnMap().entrySet()) {
                    String key = entry.getKey();
                    String col = entry.getValue();
                    // String value = entry.get(key);
                    if (!key.equals(metaInfo.getTableKey())) {
                        Object old = original.get(key);
                        Method getter = new PropertyDescriptor(key, bean.getClass()).getReadMethod();
                        Object val = getter.invoke(bean);
                        if (old != val
                            && ((val != null && !val.equals(old)) || !old.equals(val))) {
                            updateData.put(col, val);
                        }
                    }
                }
            } else {
                for (Map.Entry<String, String> entry : metaInfo.getFieldColumnMap().entrySet()) {
                    String key = entry.getValue();

                    Method getter = new PropertyDescriptor(metaInfo.getTableKey(), bean.getClass()).getReadMethod();
                    Object val = getter.invoke(bean);
                    updateData.put(key, val);
                }
            }

            //
            if (!updateData.isEmpty()) {
                for (Map.Entry<String, Object> entry : updateData.entrySet()) {
                    query.set((wrapper) -> {
                        wrapper.set(entry.getKey(), entry.getValue());
                    });
                }

                Method getter = new PropertyDescriptor(metaInfo.getTableKey(), bean.getClass()).getReadMethod();
                Object val = getter.invoke(bean);

                query.where(wrapper -> {
                        wrapper.where(metaInfo.getTableKey(), val);
                    })
                    .update();
            }

        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Long delete(T bean) {
        var metaInfo = BeanFactory.getMetaInfo(getBeanType());

        var manager = DatabaseManager.getInstance();
        var deleteQuery = manager.deleteQuery();

        var query = deleteQuery
            .table(metaInfo.getTableName());

        try {
            Method getter = new PropertyDescriptor(metaInfo.getTableKey(), bean.getClass()).getReadMethod();
            Object val = getter.invoke(bean);

            return query.where(wrapper -> {
                    wrapper.where(metaInfo.getTableKey(), val);
                })
                .delete();

        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<T> findById(Object id) throws Exception {
        var beanType = getBeanType();

        var info = BeanFactory.getMetaInfo(beanType);

        var manager = DatabaseManager.getInstance();
        var selectQuery = manager.selectQuery();
        var opt = selectQuery
            .table(info.getTableName())
            .where((wrapper) -> {
                wrapper.where(info.getTableKey(), id);
            })
            .first()
        ;
        if (opt.isPresent()) {
            var map = opt.get();
            var proxy = (T)BeanFactory.mapToProxyBean(map, beanType);

            return Optional.of(proxy);
        } else {
            return Optional.empty();
        }
    }

    public Class<?> getBeanType() {
        return GenericTypeResolver.resolveTypeArguments(getClass(), Model.class)[0];
    }
}
