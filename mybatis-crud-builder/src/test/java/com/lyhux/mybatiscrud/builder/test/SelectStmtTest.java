package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.QueryBuilder;
import com.lyhux.mybatiscrud.builder.grammar.SelectStmt;
import org.junit.jupiter.api.Test;

public class SelectStmtTest extends MysqlGrammarTest {
    @Test
    public void testSimple() {
        var builder = new QueryBuilder();

        builder.select("*")
                .from("users")
                .where((query) -> {
                    query.where("id", 123)
                            .where("username", "test")
                            ;
                })
        ;

        print(builder.toSelectStmt());
    }

    @Test
    public void testSubQuery() {
        var builder = new QueryBuilder();

        var table = new QueryBuilder().select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from(table.toSelectStmt(), "tb1")
        ;

        print(builder.toSelectStmt());
    }

    @Test
    public void testJoin() {
        var builder = new QueryBuilder();

        var table = new QueryBuilder();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from("users")
                .join("orders", "users.id", "=", "orders.user_id")
        ;

        print(builder.toSelectStmt());
    }

    @Test
    public void testJoin2() {

        var selector = new QueryBuilder();

        selector.select("*")
               .from("users")
               .join("contacts", (join) -> {
                   join.on("users.id", "=", "contacts.user_id")
                       .on("users.id", "=", "customs.user_id")
                       .where("contacts.user_id", ">", 5);
               })
        ;

        print(selector.toSelectStmt());
    }

    @Test
    public void testJoinSub() {
        var latestPosts = new QueryBuilder().from("posts")
                                          .select("user_id")
                                          .where((query) -> {
                                              query.where("is_published", 1);
                                          })
                                          .groupBy("user_id");

        var selector = new QueryBuilder();

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

        print(selector.toSelectStmt());
    }

    @Test
    public void testLeftJoin() {
        var builder = new QueryBuilder();

        var table = new QueryBuilder();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from("users")
                .leftJoin("orders", "users.id", "=", "orders.user_id")
        ;

        print(builder.toSelectStmt());
    }

    @Test
    public void testRightJoin() {
        var builder = new QueryBuilder();

        var table = new QueryBuilder();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from("users")
                .leftJoin("orders", "users.id", "=", "orders.user_id")
                .rightJoin("orders", "users.id", "=", "orders.user_id")
        ;

        print(builder.toSelectStmt());
    }

    @Test
    public void testSubQueryWithJoin() {
        var builder = new QueryBuilder();

        var table = new QueryBuilder();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from(table, "tb1")
                .join("orders", "users.id", "=", "orders.user_id")
        ;

        print(builder.toSelectStmt());
    }

    @Test
    public void testGroupBy() {
        var table = new QueryBuilder();
        table
                .from("users")
                .where((query) -> { query.where("id", ">", 123);})
                .groupBy("role", "type")
                .having((query)-> {
                    query.where("cnt", ">", 456);
                })
        ;

        print(table.toSelectStmt());
    }

    @Test
    public void testOrderBy() {
        var table = new QueryBuilder();
        table
                .from("users")
                .where((query) -> { query.where("id", ">", 123);})
                .orderBy("id", "desc")
                .orderBy("name", "asc")

        ;

        print(table.toSelectStmt());
    }

    @Test
    public void testLimit() {
        var table = new QueryBuilder();
        table
                .from("users")
                .orderBy("name", "asc")
                .limit(10)
        ;

        print(table.toSelectStmt());

        var table1 = new QueryBuilder();
        table1
                .select("user.id", "user.name", "orders.id AS order_id")
                .selectRaw("orders.no AS order_no")
                .from("users")
                .from("orders", "ord")
                .orderBy("name", "asc")
                .limit(10, 5)
        ;

        print(table1.toSelectStmt());
    }

    @Test
    public void testWhereExists() {
        var orders = new QueryBuilder() ;
        orders.selectRaw("1")
                .from("orders")
                .where((query) -> {
                    query.whereColumn("orders.user_id",  "users.id");
                });

        var users = new QueryBuilder();
        users.from("users")
                .where((query) -> {
                    query.whereExists(orders);
                });

        print(users.toSelectStmt());
    }

    @Test
    public void testUnion() {
        var order1 = new QueryBuilder();
        order1.selectRaw("1")
            .from("orders")
            .where((query) -> {
                query.whereColumn("orders.user_id",  "users.id").where((wrapper) -> {
                    wrapper.whereColumn("orders.order_id",  "orders.id");
                });

            });

        var order2 = new QueryBuilder();
        order2.from("users")
            .where((query) -> {
                query.where("user_id", ">", 123);
            });

        var order3 = new QueryBuilder();
        order3.from("users")
            .union(order1)
            .union(order2)
            .where((query) -> {
                query.where("user_id", ">", 123);
            });

        print(order3.toSelectStmt());
    }
}
