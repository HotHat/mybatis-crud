package com.lyhux.mybatiscrud.model.test;

import com.lyhux.mybatiscrud.builder.grammar.TypeValue;
import com.lyhux.mybatiscrud.model.Builder;
import com.lyhux.mybatiscrud.model.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

public class BuilderTest {
    static final String DB_URL = "jdbc:mysql://localhost/xapp";
    static final String USER = "root";
    static final String PASSWORD = "123456";
    private Builder builder;

    @BeforeEach
    public void initDB() throws SQLException {
        DbInit.initDb();
        builder = DatabaseManager.getInstance().builder();
    }

    @Test
    public void TestSelect1() throws Exception {
        var user = builder
                          .table("users")
                          .where(wrapper -> wrapper.where("id", 1))
                          .get()
            ;
        System.out.println(user);
    }

    @Test
    public void TestSelect2() throws Exception {
        var user = builder
            .table("users")
            .where(wrapper -> wrapper.where("id", 1))
            .get(UserBean.class)
            ;
        System.out.println(user);
    }

    @Test
    public void TestWhen() throws Exception {
        var result = builder
            .table("users")
            .when(true, wrapper -> wrapper.where(wp -> wp.where("id", 1)))
            .dump()
            ;
        G.assertEquals(result, "SELECT * FROM `users` WHERE `id` = ?",
            List.of(
                TypeValue.of(1)
                ));

        var result2 = builder
            .table("users")
            .when(false, wrapper -> wrapper.where(wp -> wp.where("id", 1)))
            .dump()
            ;
        G.assertEquals(result2, "SELECT * FROM `users`");

    }

    @Test
    public void TestPaginate() throws Exception {
        var page = builder
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
