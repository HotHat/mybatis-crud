package com.lyhux.mybatiscrud.test;

import com.lyhux.mybatiscrud.bean.BeanFactory;
import com.lyhux.mybatiscrud.bean.annotation.KeyType;
import com.lyhux.mybatiscrud.builder.grammar.TypeValue;
import com.lyhux.mybatiscrud.builder.vendor.MysqlGrammar;
import com.lyhux.mybatiscrud.model.Database;
import com.lyhux.mybatiscrud.model.DatabaseManager;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import org.junit.jupiter.api.Test;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
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
    public void testBuilder() {
        var db = new Database(null, new MysqlGrammar());
        var test1 = true;
        var test2 = false;

        var selector = db.selectQuery()
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

        var result = selector.select();

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

    private <T> T cast(Class<T> clazz, Object obj) {
        return (T) clazz.cast(obj);
    }

    @Test
    public void testModelInsert() throws SQLException {

        Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);

        DatabaseManager.initManager(conn, new MysqlGrammar());

        var info = BeanFactory.getMetaInfo(UserBean.class);
        System.out.printf("table mate info: %s\n",  info);

        UserBean bean = new UserBean();
        // bean.setId(1);
        bean.setUsername("model");
        bean.setPassword("password11");
        bean.setEmail("model@lyhux.com");
        bean.setGender(1);
        bean.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        bean.setUpdatedAt(LocalDateTime.now());

        var manager = DatabaseManager.getInstance();
        var insertQuery = manager.insertQuery();

        // get columns
        ArrayList<String> columns = new ArrayList<>();

        for (Map.Entry<String, String> entry : info.getFieldColumnMap().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!info.getTableKey().equals(key) && info.getKeyType() == KeyType.AUTO) {
                columns.add(value);
            }
        }

        var result = insertQuery
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
            .insertGetId()
        ;
        System.out.printf("insert query key: %s\n", result);
        // System.out.printf("sql: %s\n", result.statement());
        // for (var binding : result.bindings()) {
        //     System.out.printf("%s\n", binding);
        // }
    }

}
