package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.ExprResult;
import com.lyhux.mybatiscrud.builder.grammar.Query;
import com.lyhux.mybatiscrud.builder.grammar.TypeValue;
import com.lyhux.mybatiscrud.builder.vendor.Grammar;
import com.lyhux.mybatiscrud.builder.vendor.MysqlGrammar;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SelectStmtTest {
    static final Grammar mysqlGrammar = new MysqlGrammar();
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

        G.assertEquals(
            mysqlGrammar,
            builder.toSelectStmt(),
                "SELECT * FROM `users` WHERE `id` = ? AND `username` = ?",
                List.of(
                    TypeValue.of(123),
                    TypeValue.of("test")
                )
            );
    }

    @Test
    public void testSubQuery() {
        var builder = new Query();

        var table = new Query().select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from(table.toSelectStmt(), "tb1")
        ;

        G.assertEquals(
            mysqlGrammar,
            builder.toSelectStmt(),
            "SELECT * FROM (SELECT * FROM `users` WHERE `id` > ?) AS tb1",
            List.of(
                TypeValue.of(123)
            )
        );
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

        G.assertEquals(
            mysqlGrammar,
            builder.toSelectStmt(),
            "SELECT * FROM `users` INNER JOIN `orders` ON `users`.`id` = `orders`.`user_id`"
        );
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

        G.assertEquals(
            mysqlGrammar,
            selector.toSelectStmt(),
            "SELECT * FROM `users` INNER JOIN `contacts` ON (`users`.`id` = `contacts`.`user_id` AND `users`.`id` = `customs`.`user_id` AND `contacts`.`user_id` > ?)",
            List.of(
                TypeValue.of(5)
            )
        );
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

        G.assertEquals(
            mysqlGrammar,
            selector.toSelectStmt(),
            "SELECT * FROM `users` INNER JOIN (SELECT `user_id` FROM `posts` WHERE `is_published` = ? GROUP BY `user_id`) AS latest_posts ON (`users`.`id` = `latest_posts`.`user_id`) INNER JOIN (SELECT `user_id` FROM `posts` WHERE `is_published` = ? GROUP BY `user_id`) AS latest_posts ON (`users`.`id` = `latest_posts`.`user_id`) WHERE `users`.`id` > ?",
            List.of(
                TypeValue.of(1),
                TypeValue.of(1),
                TypeValue.of(5)
            )
        );
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

        G.assertEquals(
            mysqlGrammar,
            builder.toSelectStmt(),
            "SELECT * FROM `users` LEFT JOIN `orders` ON `users`.`id` = `orders`.`user_id`"
        );
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

        G.assertEquals(
            mysqlGrammar,
            builder.toSelectStmt(),
            "SELECT * FROM `users` LEFT JOIN `orders` ON `users`.`id` = `orders`.`user_id` RIGHT JOIN `orders` ON `users`.`id` = `orders`.`user_id`"
        );
    }

    @Test
    public void testSubQueryWithJoin() {
        var builder = new Query();

        var table = new Query();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from(table, "tb1")
                .join("orders", "tb1.id", "=", "orders.user_id")
        ;

        G.assertEquals(
            mysqlGrammar,
            builder.toSelectStmt(),
            "SELECT * FROM (SELECT * FROM `users` WHERE `id` > ?) AS tb1 INNER JOIN `orders` ON `tb1`.`id` = `orders`.`user_id`",
            List.of(
                TypeValue.of(123)
            )
        );
    }

    @Test
    public void testGroupBy() {
        var table = new Query();
        table
                .select("role", "type")
                .selectRaw("count(*) AS cnt")
                .from("users")
                .where((query) -> { query.where("id", ">", 123);})
                .groupBy("role", "type")
                .having((query)-> {
                    query.where("cnt", ">", 456);
                })
        ;

        G.assertEquals(
            mysqlGrammar,
            table.toSelectStmt(),
                "SELECT `role`, `type`, count(*) AS cnt FROM `users` WHERE `id` > ? GROUP BY `role`, `type` HAVING `cnt` > ?",
                List.of(
                    TypeValue.of(123),
                    TypeValue.of(456)
                )
        );
    }

    @Test
    public void testOrderBy() {
        var table = new Query();
        table
                .from("users")
                .where((query) -> { query.where("users.id", ">", 123);})
                .orderBy("users.id", "desc")
                .orderBy("users.name", "asc")

        ;

        G.assertEquals(
            mysqlGrammar,
            table.toSelectStmt(),
            "SELECT * FROM `users` WHERE `users`.`id` > ? ORDER BY `users`.`id` DESC, `users`.`name` ASC",
            List.of(
                TypeValue.of(123)
            )
        );

        G.assertEquals(
            mysqlGrammar.setTablePrefix("t_"),
            table.toSelectStmt(),
            "SELECT * FROM `t_users` WHERE `t_users`.`id` > ? ORDER BY `t_users`.`id` DESC, `t_users`.`name` ASC",
            List.of(
                TypeValue.of(123)
            )
        );

    }

    @Test
    public void testLimit() {
        var table = new Query();
        table
                .from("users")
                .orderBy("name", "asc")
                .limit(10)
        ;

        G.assertEquals(
            mysqlGrammar,
            table.toSelectStmt(),
            "SELECT * FROM `users` ORDER BY `name` ASC LIMIT 10",
            List.of(
            )
        );

        var table1 = new Query();
        table1
                .select("user.id", "user.name", "orders.id AS order_id")
                .selectRaw("orders.no AS order_no")
                .from("users")
                .from("orders", "ord")
                .orderBy("name", "asc")
                .limit(10, 5)
        ;

        G.assertEquals(
            mysqlGrammar,
            table1.toSelectStmt(),
            "SELECT `user`.`id`, `user`.`name`, `orders`.`id` AS `order_id`, orders.no AS order_no FROM (`users`, `orders` AS `ord`) ORDER BY `name` ASC LIMIT 10 OFFSET 5"
        );
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

        G.assertEquals(
            mysqlGrammar,
            users.toSelectStmt(),
            "SELECT * FROM `users` WHERE EXISTS (SELECT 1 FROM `orders` WHERE `orders`.`user_id` = `users`.`id`)"
        );
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
                query.where("user_id", ">", 456);
            });

        var order3 = new Query();
        order3.from("users")
            .union(order1)
            .union(order2)
            .where((query) -> {
                query.where("user_id", ">", 123);
            });

        G.assertEquals(
            mysqlGrammar,
            order3.toSelectStmt(),
            "(SELECT * FROM `users` WHERE `user_id` > ?) UNION (SELECT 1 FROM `orders` WHERE `orders`.`user_id` = `users`.`id` AND (`orders`.`order_id` = `orders`.`id`)) UNION (SELECT * FROM `users` WHERE `user_id` > ?)",
            List.of(
                TypeValue.of(123),
                TypeValue.of(456)
            )
        );
    }


}
