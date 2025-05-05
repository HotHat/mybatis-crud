package com.lyhux.mybatiscrud.model.test;

import com.lyhux.mybatiscrud.builder.grammar.Query;
import com.lyhux.mybatiscrud.builder.grammar.TypeValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

public class QueryTest {
    static final String DB_URL = "jdbc:mysql://localhost/xapp";
    static final String USER = "root";
    static final String PASSWORD = "123456";

    @BeforeEach
    public void initDB() throws SQLException {
        DbInit.initDb();
    }

    @Test
    public void TestSelect1() throws Exception {
        var user = Query.of()
            .table("users")
            .where(wrapper -> wrapper.where("id", 1))
            .get()
            ;
        System.out.println(user);
    }

    @Test
    public void TestSelect2() throws Exception {
        var user = Query.of()
            .table("users")
            .where(wrapper -> wrapper.where("id", 1))
            .get(UserBean.class)
            ;
        System.out.println(user);
    }

    @Test
    public void TestWhen() throws Exception {
        var result = Query.of()
            .table("users")
            .when(true, wrapper -> wrapper.where(wp -> wp.where("id", 1)))
            .dump()
            ;
        G.assertEquals(result, "SELECT * FROM `users` WHERE `id` = ?",
            List.of(
                TypeValue.of(1)
                ));

        var result2 = Query.of()
            .table("users")
            .when(false, wrapper -> wrapper.where(wp -> wp.where("id", 1)))
            .dump()
            ;
        G.assertEquals(result2, "SELECT * FROM `users`");

    }

    @Test
    public void TestPaginate() throws Exception {
        var page = Query.of()
            .table("users")
            .where(wrapper -> wrapper.where("id", ">", 1))
            .paginate(1, 5, UserBean.class)
            ;
        System.out.println("page:" + page.page());
        System.out.println("size:" + page.pageSize());
        System.out.println("total:" + page.total());
        System.out.println("totalPages:" + page.totalPages());
        System.out.println("records:" + page.records());
    }


}
