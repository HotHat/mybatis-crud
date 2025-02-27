package test.java;

import main.java.com.lyhux.sqlbuilder.Builder;
import main.java.com.lyhux.sqlbuilder.CompileResult;
import main.java.com.lyhux.sqlbuilder.IntArray;
import main.java.com.lyhux.sqlbuilder.StrArray;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class BuilderTest {


    @Test
    void testSelect() {
        Builder b = new Builder();

        b.select("version()");
        System.out.println(b);
    }

    @Test
    void testSimpleWhere() {
        Builder b = new Builder();
        b.select("name", "age")
                .from("users")
                .where("id", "3");

        CompileResult result = b.compile();
        System.out.println(result.getSqlStmt());
        System.out.println(result.getParameter());

    }

    @Test
    void testNestWhere() {
        System.out.println("Hello, World!");

        Builder b = new Builder();
        b.select("name", "age")
                .from("User")
                .from("Admin", "adm")
                .where("username", "admin")
                .where("l0_w_1", "l0_w_1")
                .where("l0_w_2", "l0_w_2")
                .orWhere("l0_ow_1", "l0_ow_1")
                .orWhere("l0_ow_2", "l0_ow_2")
                .where((query) -> {
                    query.where("l1_w_1", "l1_w_11")
                            .where("l1_w_2", "l1_w_2")
                            .orWhere("l1_ow_1", "l1_ow_1")
                            .orWhere("l1_ow_2", "l1_ow_2")
                            .where((query1)-> {
                                query1.where("l2_w_1", "l2_w_1");
                            })
                            .where("l1_w_3", "l1_w_3")
                    ;
                })
                .where("l0_w_3", "l0_w_3")

        ;

        CompileResult result = b.compile();
        System.out.println(result.getSqlStmt());
        System.out.println(result.getParameter());
    }

    @Test
    void testWhereIn() throws SQLException {
        Builder b = new Builder();
        b.select("id", "name")
                .from("users")
                .where("id", "5")
                .whereIn("id", IntArray.asList(1, 2, 3))
                .whereIn("name", StrArray.asList("1", "2", "3"))
        ;

        CompileResult result = b.compile();
        System.out.println(result.getSqlStmt());
        System.out.println(result.getParameter());
    }

    @Test
    void testWhereInBuilder() throws SQLException {
        Builder a = new Builder();
        a.select("id")
                .from("users")
                .whereIn("id", IntArray.asList(1, 2, 3));

        Builder b = new Builder();
        b.select("id", "name")
                .from("users")
                .whereIn("id", a)
        ;

        CompileResult result = b.compile();
        System.out.println(result.getSqlStmt());
        System.out.println(result.getParameter());
    }

    @Test
    void testJoin() throws SQLException {
        Builder b = new Builder();
        b.select("id", "name")
                .from("users")
                .join("orders", "orders.user_id", "=", "users.id");
        System.out.println(b);
    }
}