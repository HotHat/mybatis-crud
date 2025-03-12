package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.SelectStmt;
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
    public void testJoin2() {

        var selector = new SelectStmt();

        selector.select("*")
               .from("users")
               .join("contacts", (join) -> {
                   join.on("users.id", "=", "contacts.user_id")
                       .on("users.id", "=", "customs.user_id")
                       .where("contacts.user_id", ">", 5);
               })
        ;

        print(selector);
    }

    @Test
    public void testJoinSub() {
        var latestPosts = new SelectStmt().from("posts")
                                          .select("user_id")
                                          .where((query) -> {
                                              query.where("is_published", 1);
                                          })
                                          .groupBy("user_id");

        var selector = new SelectStmt();

        selector.from("users")
                .joinSub(latestPosts, "latest_posts", (join) -> {
                    join.on("users.id", "=", "latest_posts.user_id");
                })
                .joinSub(latestPosts, "latest_posts", (join) -> {
                    join.on("users.id", "=", "latest_posts.user_id");
                })
            .where((query) -> {
                query.where("users.id", ">", 5);
            })
        ;

        print(selector);
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

    @Test
    public void testWhereExists() {
        var orders = new SelectStmt();
        orders.selectRaw("1")
                .from("orders")
                .where((query) -> {
                    query.whereColumn("orders.user_id",  "users.id");
                });

        var users = new SelectStmt();
        users.from("users")
                .where((query) -> {
                    query.whereExists(orders);
                });

        print(users);
    }

    @Test
    public void testUnion() {
        var order1 = new SelectStmt();
        order1.selectRaw("1")
            .from("orders")
            .where((query) -> {
                query.whereColumn("orders.user_id",  "users.id").where((wrapper) -> {
                    wrapper.whereColumn("orders.order_id",  "orders.id");
                });

            });

        var order2 = new SelectStmt();
        order2.from("users")
            .where((query) -> {
                query.where("user_id", ">", 123);
            });

        var order3 = new SelectStmt();
        order3.from("users")
            .union(order1)
            .union(order2)
            .where((query) -> {
                query.where("user_id", ">", 123);
            });

        print(order3);
    }
}
