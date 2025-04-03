package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.Query;
import org.junit.jupiter.api.Test;

public class SelectStmtTest extends MysqlGrammarTest {
    @Test
    public void testSimple() {
        var builder = new Query();

        builder.select("*")
                .from("users")
                .where((query) -> {
                    query.where("id", 123)
                            .where("username", "test")
                            ;
                })
        ;

        exprAssert(builder.toSelectStmt());
    }

    @Test
    public void testSubQuery() {
        var builder = new Query();

        var table = new Query().select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from(table.toSelectStmt(), "tb1")
        ;

        exprAssert(builder.toSelectStmt());
    }

    @Test
    public void testJoin() {
        var builder = new Query();

        var table = new Query();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from("users")
                .join("orders", "users.id", "=", "orders.user_id")
        ;

        exprAssert(builder.toSelectStmt());
    }

    @Test
    public void testJoin2() {

        var selector = new Query();

        selector.select("*")
               .from("users")
               .join("contacts", (join) -> {
                   join.on("users.id", "=", "contacts.user_id")
                       .on("users.id", "=", "customs.user_id")
                       .where("contacts.user_id", ">", 5);
               })
        ;

        exprAssert(selector.toSelectStmt());
    }

    @Test
    public void testJoinSub() {
        var latestPosts = new Query().from("posts")
                                          .select("user_id")
                                          .where((query) -> {
                                              query.where("is_published", 1);
                                          })
                                          .groupBy("user_id");

        var selector = new Query();

        selector.from("users")
                .joinSub(latestPosts.toSelectStmt(), "latest_posts", (join) -> {
                    join.on("users.id", "=", "latest_posts.user_id");
                })
                .joinSub(latestPosts.toSelectStmt(), "latest_posts", (join) -> {
                    join.on("users.id", "=", "latest_posts.user_id");
                })
            .where((query) -> {
                query.where("users.id", ">", 5);
            })
        ;

        exprAssert(selector.toSelectStmt());
    }

    @Test
    public void testLeftJoin() {
        var builder = new Query();

        var table = new Query();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from("users")
                .leftJoin("orders", "users.id", "=", "orders.user_id")
        ;

        exprAssert(builder.toSelectStmt());
    }

    @Test
    public void testRightJoin() {
        var builder = new Query();

        var table = new Query();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from("users")
                .leftJoin("orders", "users.id", "=", "orders.user_id")
                .rightJoin("orders", "users.id", "=", "orders.user_id")
        ;

        exprAssert(builder.toSelectStmt());
    }

    @Test
    public void testSubQueryWithJoin() {
        var builder = new Query();

        var table = new Query();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from(table, "tb1")
                .join("orders", "users.id", "=", "orders.user_id")
        ;

        exprAssert(builder.toSelectStmt());
    }

    @Test
    public void testGroupBy() {
        var table = new Query();
        table
                .from("users")
                .where((query) -> { query.where("id", ">", 123);})
                .groupBy("role", "type")
                .having((query)-> {
                    query.where("cnt", ">", 456);
                })
        ;

        exprAssert(table.toSelectStmt());
    }

    @Test
    public void testOrderBy() {
        var table = new Query();
        table
                .from("users")
                .where((query) -> { query.where("id", ">", 123);})
                .orderBy("id", "desc")
                .orderBy("name", "asc")

        ;

        exprAssert(table.toSelectStmt());
    }

    @Test
    public void testLimit() {
        var table = new Query();
        table
                .from("users")
                .orderBy("name", "asc")
                .limit(10)
        ;

        exprAssert(table.toSelectStmt());

        var table1 = new Query();
        table1
                .select("user.id", "user.name", "orders.id AS order_id")
                .selectRaw("orders.no AS order_no")
                .from("users")
                .from("orders", "ord")
                .orderBy("name", "asc")
                .limit(10, 5)
        ;

        exprAssert(table1.toSelectStmt());
    }

    @Test
    public void testWhereExists() {
        var orders = new Query() ;
        orders.selectRaw("1")
                .from("orders")
                .where((query) -> {
                    query.whereColumn("orders.user_id",  "users.id");
                });

        var users = new Query();
        users.from("users")
                .where((query) -> {
                    query.whereExists(orders);
                });

        exprAssert(users.toSelectStmt());
    }

    @Test
    public void testUnion() {
        var order1 = new Query();
        order1.selectRaw("1")
            .from("orders")
            .where((query) -> {
                query.whereColumn("orders.user_id",  "users.id").where((wrapper) -> {
                    wrapper.whereColumn("orders.order_id",  "orders.id");
                });

            });

        var order2 = new Query();
        order2.from("users")
            .where((query) -> {
                query.where("user_id", ">", 123);
            });

        var order3 = new Query();
        order3.from("users")
            .union(order1)
            .union(order2)
            .where((query) -> {
                query.where("user_id", ">", 123);
            });

        exprAssert(order3.toSelectStmt());
    }
}
