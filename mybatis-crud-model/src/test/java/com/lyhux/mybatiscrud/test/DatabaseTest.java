package com.lyhux.mybatiscrud.test;

import com.lyhux.mybatiscrud.bean.BeanFactory;
import com.lyhux.mybatiscrud.bean.annotation.KeyType;
import com.lyhux.mybatiscrud.builder.grammar.QueryBuilder;
import com.lyhux.mybatiscrud.builder.grammar.TypeValue;
import com.lyhux.mybatiscrud.builder.vendor.MysqlGrammar;
import com.lyhux.mybatiscrud.model.Database;
import com.lyhux.mybatiscrud.model.DatabaseManager;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import org.junit.jupiter.api.Test;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class DatabaseTest {
    static final String DB_URL = "jdbc:mysql://localhost/xapp";
    static final String USER = "root";
    static final String PASSWORD = "123456";

    @Test
    public void testBuilder() throws Exception {
        var db = new Database(null, new MysqlGrammar());
        var test1 = true;
        var test2 = false;

        var selector = new QueryBuilder()
            .select()
            .from("users")
            .select("id", "username", "age")
            .where((query) -> {
                query.where("id", 1)
                     .whereIn("username", "haha", "baba")
                     .when(test1, (q) -> {
                         q.where("id", 3).orWhere("age", 2);
                     })
                     .when(test2, (q) -> {
                         q.where("id", 4).where("age", 1);
                     })
                ;
            })

        ;

        var result = db.adapter().query(selector).get();

        // selector.replaceSelect()
        //     .selectRaw("count(*) as aggregate");
        //
        // result = builder.compile();
        // System.out.println(result);
        // selector.recoverSelect();
        // result = builder.compile();
        // System.out.println(result);




        /*
        var res = builder.select((stmt) -> {
            stmt.from("user")
                .select("id", "name", "age")
                .where((query) -> {
                    query.where("id", 1)
                         .whereIn("name", "haha", "baba")
                         .when(test1, (q) -> {
                             q.where("id", 3);
                         })
                         .when(test2, (q) -> {
                             q.where("id", 4);
                         })
                    ;
                })

            ;
        }).compile();

        System.out.println(res);
         */
    }

    @Test
    public void testModelInsert() throws SQLException {

        Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);

        DatabaseManager.initManager(conn, new MysqlGrammar());

        var info = BeanFactory.getMetaInfo(UserBean.class);
        System.out.printf("table mate info: %s\n",  info);

        UserBean bean = new UserBean();
        // bean.setId(14);
        bean.setUsername("model15");
        bean.setPassword("password11");
        bean.setEmail("model@lyhux.com");
        bean.setGender(1);
        bean.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        bean.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        var manager = DatabaseManager.getInstance();
        var insertQuery = manager.adapter();

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

            Long primaryKey = insertQuery
                .query(query -> {
                    query
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
                        })
                        ;
                })

                .insertGetId()
                ;

            if (info.isPrimaryKeyInteger() || info.isPrimaryKeyLong()) {
                Method setter = new PropertyDescriptor(info.getTableKey(), bean.getClass()).getWriteMethod();
                if (info.isPrimaryKeyInteger()) {
                    setter.invoke(bean, Math.toIntExact(primaryKey));
                } else {
                    setter.invoke(bean, primaryKey);
                }

            }

            System.out.printf("insert query key: %s\n", primaryKey);
            System.out.printf("bean: %s\n", bean);

        } catch ( IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        // System.out.printf("sql: %s\n", result.statement());
        // for (var binding : result.bindings()) {
        //     System.out.printf("%s\n", binding);
        // }
    }

    @Test
    public void testUserModelInsert() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        DatabaseManager.initManager(conn, new MysqlGrammar());

        UserBean bean = new UserBean();
        bean.setId(21L);
        bean.setUsername("user_model");
        bean.setPassword("password11");
        bean.setEmail("model@lyhux.com");
        bean.setGender(1);
        bean.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        bean.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        var userModel = new UserModel();

        System.out.printf("bean before insert %s\n", bean);
        userModel.insert(bean);


        System.out.printf("bean after insert: %s\n", bean);
    }

    @Test
    public void testUserModelFindById() throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        DatabaseManager.initManager(conn, new MysqlGrammar());
        var userModel = new UserModel();

        var opt = userModel.findById(9);
        System.out.printf("bean after findById: %s\n", opt);

        if (opt.isPresent()) {
            var user = opt.get();
            user.setUsername("update test");
            user.setEmail("13323@qq.com");

            userModel.update(user);
        }
    }

    private void initDb() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        DatabaseManager.initManager(conn, new MysqlGrammar());
    }

    @Test
    public void testUserModelFindAll() throws Exception {
        initDb();
        var userModel = new UserModel();

        UserBean bean = new UserBean();
        bean.setId(21L);
        long count = userModel.delete(bean);
        System.out.printf("delete count: %s\n", count);


    }



}
