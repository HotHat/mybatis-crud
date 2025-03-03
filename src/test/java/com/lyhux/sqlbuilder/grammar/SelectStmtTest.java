package com.lyhux.sqlbuilder.grammar;

import org.junit.jupiter.api.Test;

public class SelectStmtTest extends MysqlGrammarTest {
    @Test
    public void testSimple() {
        var builder = new SelectStmt();

        builder.select("*")
                .from("users")
                .where((query) -> {
                    query.where("id", 123)
                            .where("username", "test")
                            ;
                })
        ;

        print(builder);
    }

    @Test
    public void testSubQuery() {
        var builder = new SelectStmt();

        var table = new SelectStmt();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from(table, "tb1")
        ;

        print(builder);
    }

    @Test
    public void testJoin() {
        var builder = new SelectStmt();

        var table = new SelectStmt();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from("users")
                .join("orders", "users.id", "=", "orders.user_id")
        ;

        print(builder);
    }

    @Test
    public void testLeftJoin() {
        var builder = new SelectStmt();

        var table = new SelectStmt();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from("users")
                .leftJoin("orders", "users.id", "=", "orders.user_id")
        ;

        print(builder);
    }

    @Test
    public void testRightJoin() {
        var builder = new SelectStmt();

        var table = new SelectStmt();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from("users")
                .leftJoin("orders", "users.id", "=", "orders.user_id")
                .rightJoin("orders", "users.id", "=", "orders.user_id")
        ;

        print(builder);
    }

    @Test
    public void testSubQueryWithJoin() {
        var builder = new SelectStmt();

        var table = new SelectStmt();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from(table, "tb1")
                .join("orders", "users.id", "=", "orders.user_id")
        ;

        print(builder);
    }

    @Test
    public void testGroupBy() {
        var table = new SelectStmt();
        table
                .from("users")
                .where((query) -> { query.where("id", ">", 123);})
                .groupBy("role", "type")
                .having((query)-> {
                    query.where("cnt", ">", 456);
                })
        ;

        print(table);
    }

    @Test
    public void testOrderBy() {
        var table = new SelectStmt();
        table
                .from("users")
                .where((query) -> { query.where("id", ">", 123);})
                .orderBy("id", "desc")
                .orderBy("name", "asc")

        ;

        print(table);
    }

    @Test
    public void testLimit() {
        var table = new SelectStmt();
        table
                .from("users")
                .orderBy("name", "asc")
                .limit(10)
        ;

        print(table);

        var table1 = new SelectStmt();
        table1
                .select("user.id", "user.name", "orders.id AS order_id")
                .selectRaw("orders.no AS order_no")
                .from("users")
                .from("orders", "ord")
                .orderBy("name", "asc")
                .limit(10, 5)
        ;

        print(table1);
    }
}
