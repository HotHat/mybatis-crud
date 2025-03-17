package com.lyhux.mybatiscrud.test;

import com.lyhux.mybatiscrud.builder.vendor.MysqlGrammar;
import com.lyhux.mybatiscrud.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AdapterTest {
    static final String DB_URL = "jdbc:mysql://localhost/xapp";
    static final String USER = "root";
    static final String PASSWORD = "";

    private Connection conn;

    @BeforeEach
    public void setUp() throws Exception {

        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

        conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }


    @AfterEach
    protected void tearDown() throws Exception {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    public void testInsert() throws SQLException {
        var adapter = new InsertAdapter(conn, new MysqlGrammar());

        Long primaryKey = adapter
               .table("users")
               .columns("username", "password", "gender", "email", "created_at", "updated_at")
               .values((group -> {
                   group
                       .add("momo5")
                       .add("123456")
                       .add(2)
                       .add("yaha2@gmail.com")
                       .add(LocalDateTime.now())
                       // .add(Timestamp.from(Instant.now()))
                       .addNull();

               }))
            .insertGetId();

        System.out.printf("Inserted record: %s\n", primaryKey);
    }

    @Test
    public void testUpdate() throws SQLException {
        var adapter = new UpdateAdapter(conn, new MysqlGrammar());
        var rows = adapter
            .table("users")
            .set((wrapper) -> {
                wrapper
                    .set("username", "linux")
                    .setRaw("email", "hello@gmail.com")
                ;

            })
            .where(wrapper -> {
                wrapper.where("id", ">", 1);
            })
            .update();

        System.out.printf("Updated record count: %s\n", rows);

    }

    @Test
    public void testDelete() throws SQLException {
        var adapter = new DeleteAdapter(conn, new MysqlGrammar());
        var rows = adapter
            .table("users")
            .where(wrapper -> {
                wrapper.where("id", ">", 3);
            })
            .delete();

        System.out.printf("Updated record count: %s\n", rows);

    }

    @Test
    public void testSelect() throws Exception {

        var adapter = new SelectAdapter(conn, new MysqlGrammar());
        var lst = adapter
            .from("users")
            .get();

        System.out.println(lst);

        adapter = new SelectAdapter(conn, new MysqlGrammar());

        var user = adapter
            .from("users")
            .first();

        // var result = user.getQuery();
        // System.out.printf("Selected record: %s\n%s", result.statement(), result.bindings());

        System.out.println(user);
    }

    @Test
    public void testSelectQualifier() throws Exception {

        var adapter = new SelectAdapter(conn, new MysqlGrammar());
        var lst = adapter
            .qualifier(UserBean.class)
            .from("users")
            .getQualifier();

        for (var item : lst) {
            UserBean bean = (UserBean) item;
            System.out.printf("UserBean: %s, %s\n", bean.getId(), bean.getUsername());
        }
        // System.out.println(lst);
    }


    @Test
    public void testTransaction() throws SQLException {
        var db = new Database(conn, new MysqlGrammar());
        db.beginTransaction();

        var rowCount = db.updateQuery()
               .table("users")
               .set((wrapper) -> {
                   wrapper.set("username", "tr1");
               })
               .where(wrapper -> {
                   wrapper.where("id", 6);
               }).update();

        System.out.printf("Updated record count: %s\n", rowCount);

        rowCount = db.updateQuery()
               .table("users")
               .set((wrapper) -> {
                   wrapper.set("username", "tr2");
               })
               .where(wrapper -> {
                   wrapper.where("id", 7);
               }).update();
        System.out.printf("Updated record count: %s\n", rowCount);

        db.commit();
    }


    @Test
    public void testTransactionLevels() throws SQLException {
        var builder = new Database(conn, new MysqlGrammar());
        // level 1
        builder.beginTransaction();
            // level 2 commit
            builder.beginTransaction();
                // level rollback
                builder.beginTransaction();
        var rowCount = builder.updateQuery()
                          .table("users")
                          .set((wrapper) -> {
                              wrapper.set("username", "rollback");
                          })
                          .where(wrapper -> {
                              wrapper.where("id", 8);
                          }).update();
        System.out.printf("transaction rollback count: %s\n", rowCount);

                builder.rollback();
        rowCount = builder.updateQuery()
                          .table("users")
                          .set((wrapper) -> {
                              wrapper.set("username", "commit");
                          })
                          .where(wrapper -> {
                              wrapper.where("id", 9);
                          }).update();
        System.out.printf("transaction commit count: %s\n", rowCount);

            builder.commit();

        // end top
        builder.commit();

        // after transaction
        rowCount = builder.updateQuery()
                          .table("users")
                          .set((wrapper) -> {
                              wrapper.set("username", "commit");
                          })
                          .where(wrapper -> {
                              wrapper.where("id", 6);
                          }).update();
        System.out.printf("auto commit count: %s\n", rowCount);
    }

    @Test
    public void testGetMapToBean() throws Exception {
        var db = new Database(conn, new MysqlGrammar());
        var selector = db.selectQuery();

        var lst = selector
            .from("users")
            .get(UserBean.class);

        for (var row : lst) {
            System.out.println(row);
        }

        var user = db.selectQuery()
            .from("users")
            .first(UserBean.class);

        System.out.printf("user: %s\n", user);
    }
}
