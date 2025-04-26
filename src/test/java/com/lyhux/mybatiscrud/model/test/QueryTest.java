package com.lyhux.mybatiscrud.model.test;

import com.lyhux.mybatiscrud.builder.grammar.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class QueryTest {
    static final String DB_URL = "jdbc:mysql://localhost/xapp";
    static final String USER = "root";
    static final String PASSWORD = "";

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
