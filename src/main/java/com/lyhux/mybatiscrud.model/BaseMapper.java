package com.lyhux.mybatiscrud.model;

import com.lyhux.mybatiscrud.bean.BeanFactory;
import com.lyhux.mybatiscrud.bean.annotation.KeyType;
import com.lyhux.mybatiscrud.builder.grammar.Query;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;

public class BaseMapper<T> implements Mapper<T> {
    public void insert(T bean) {
        insertBean(List.of(bean), false);
    }

    public void insertBatch(List<T> beans) {
        insertBean(beans, true);
    }

    private void insertBean(List<T> beans, boolean isBatch) {
        if (beans == null || beans.isEmpty()) { return; }
        // var type = GenericTypeResolver.resolveTypeArguments(getClass(), Model.class);
        var bean = beans.getFirst();

        var info = BeanFactory.getMetaInfo(bean.getClass());
        // System.out.printf("table mate info: %s\n",  info);

        var manager = DatabaseManager.getInstance();
        var insertQuery = manager.adapter();

        // get columns
        ArrayList<String> columns = new ArrayList<>();

        try {
            // Bean table map info
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
                .query(builder -> {
                    builder
                        .table(info.getTableName())
                        .columns(columns.toArray(new String[0]));

                    for (var addBean: beans) {
                        builder.values((wrapper) -> {
                            try {
                                for (Map.Entry<String, String> entry : info.getFieldColumnMap().entrySet()) {
                                    String key = entry.getKey();
                                    String value = entry.getValue();
                                    if (columns.contains(value)) {
                                        Method getter = new PropertyDescriptor(key, addBean.getClass()).getReadMethod();
                                        // Class<?> type = getter.getReturnType();
                                        Object val = getter.invoke(addBean);
                                        wrapper.add(val);
                                    }
                                }
                            } catch (Exception  e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                });


            if (!isBatch
                && (info.isPrimaryKeyInteger() || info.isPrimaryKeyLong())
                && info.getKeyType() == KeyType.AUTO)
            {
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
        var updateQuery = manager.adapter();


        var query = new Query()
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
                    });
                //
                updateQuery.query(query).update();
            }

        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Long delete(T bean) {
        var metaInfo = BeanFactory.getMetaInfo(getBeanType());

        var manager = DatabaseManager.getInstance();
        var deleteQuery = manager.adapter();

        var query = new Query()
            .table(metaInfo.getTableName());

        try {
            Method getter = new PropertyDescriptor(metaInfo.getTableKey(), bean.getClass()).getReadMethod();
            Object val = getter.invoke(bean);

            query.where(wrapper -> {
                    wrapper.where(metaInfo.getTableKey(), val);
                });

            return deleteQuery.query(query).delete();

        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<T> findById(Object id) throws Exception {
        var beanType = getBeanType();

        var info = BeanFactory.getMetaInfo(beanType);

        var manager = DatabaseManager.getInstance();
        var selectQuery = manager.adapter();
        var opt =  selectQuery
            .query(query -> {
                query
                    .table(info.getTableName())
                    .where((wrapper) -> {
                        wrapper.where(info.getTableKey(), id);
                    });
            })
            .first();

        if (opt.isPresent()) {
            var map = opt.get();
            var proxy = (T)BeanFactory.mapToProxyBean(map, beanType);

            return Optional.of(proxy);
        } else {
            return Optional.empty();
        }
    }

    public List<T> findAll() throws Exception {
        var beanType = getBeanType();

        var info = BeanFactory.getMetaInfo(beanType);

        var manager = DatabaseManager.getInstance();
        var selectQuery = manager.adapter();
        var lst =  selectQuery
            .query(query -> {
                query.table(info.getTableName());
            })
            .get();

        List<T> result = new ArrayList<>();

        for (var map : lst) {
            var proxy = (T)BeanFactory.mapToProxyBean(map, beanType);
            result.add(proxy);
        }

        return result;
    }

    public List<T> query(Query builder) throws Exception {
        var beanType = getBeanType();

        var info = BeanFactory.getMetaInfo(beanType);

        var manager = DatabaseManager.getInstance();
        var selectQuery = manager.adapter();

        builder.table(info.getTableName());

        var lst =  selectQuery
            .query(builder)
            .get();

        List<T> result = new ArrayList<>();

        for (var map : lst) {
            var proxy = (T)BeanFactory.mapToProxyBean(map, beanType);
            result.add(proxy);
        }

        return result;
    }

    // public Page<T> paginate(Query builder) throws Exception {
    //     var beanType = getBeanType();
    //
    //     var info = BeanFactory.getMetaInfo(beanType);
    //
    //     var manager = DatabaseManager.getInstance();
    //     var selectQuery = manager.adapter();
    //
    //     builder.table(info.getTableName());
    //
    //     var pageRecord =  selectQuery
    //         .query(builder)
    //         .paginate();
    //
    //     List<T> result = new ArrayList<>();
    //
    //     for (var map : pageRecord.records()) {
    //         var proxy = (T)BeanFactory.mapToProxyBean(map, beanType);
    //         result.add(proxy);
    //     }
    //
    //     return new Page<>(pageRecord.page(), pageRecord.pageSize(), pageRecord.total(), result);
    // }

    public Long count(Query builder) throws Exception {
        var beanType = getBeanType();
        var info = BeanFactory.getMetaInfo(beanType);

        var manager = DatabaseManager.getInstance();
        var selectQuery = manager.adapter();

        if (builder == null) {
            builder = new Query();
        }

        builder.table(info.getTableName());
        return selectQuery
            .query(builder)
            .count();
    }

    private Class<?> getBeanType() {
        return GenericTypeResolver.resolveTypeArguments(getClass(), Mapper.class)[0];
    }
}
