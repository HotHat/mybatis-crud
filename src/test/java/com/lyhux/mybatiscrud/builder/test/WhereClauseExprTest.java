package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.*;
import com.lyhux.mybatiscrud.builder.vendor.Grammar;
import com.lyhux.mybatiscrud.builder.vendor.MysqlGrammar;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class WhereClauseExprTest {
    static final Grammar mysqlGrammar = new MysqlGrammar();

    @Test
    public void testWhereClauseExpr() {
        var expr = new WhereExpr(true);
        expr.add(new BinaryExpr(
            new EscapedStr("id"),
            "=",
            new BindingValue<>(new RawStr("?"),
                List.of(new TypeValue<>(JDBCType.INTEGER, 123)))
        ), "AND");

        expr.where("name", "mac");

        G.assertEquals(
            mysqlGrammar,
            expr,
            "(`id` = ? AND `name` = ?)",
            List.of(
                TypeValue.of(123),
                TypeValue.of("mac")
            )
        );
    }

    @Test
    public void testWhereExprNest() {
        var expr = new WhereExpr();

        expr.where("id", "123")
            .where((query) -> {
                query.where("l1_w0", "l1_w0")
                     .where("l1_w1", "l1_w1")
                     .where((q2) -> {
                         q2.where("l2_w0", "l2_w0")
                           .where("l2_w1", "l2_w1");
                     })
                     .where("l1_w3", "l1_w3");
            })
            .where("l0_w2", "l0_w2");

        G.assertEquals(
            mysqlGrammar,
            expr,
            "`id` = ? AND (`l1_w0` = ? AND `l1_w1` = ? AND (`l2_w0` = ? AND `l2_w1` = ?) AND `l1_w3` = ?) AND `l0_w2` = ?",
            List.of(
                TypeValue.of("123"),
                TypeValue.of("l1_w0"),
                TypeValue.of("l1_w1"),
                TypeValue.of("l2_w0"),
                TypeValue.of("l2_w1"),
                TypeValue.of("l1_w3"),
                TypeValue.of("l0_w2")
            )
        );
    }

    @Test
    public void testNestWhere1() {
        var expr = new WhereExpr();

        expr.where("r1", "r1")
            .orWhere("r2", "r2")
            .where("r3", "r3")
            .orWhere("r4", "r4")
        ;

        G.assertEquals(
            mysqlGrammar,
            expr,
            "`r1` = ? OR `r2` = ? AND `r3` = ? OR `r4` = ?",
            List.of(
                TypeValue.of("r1"),
                TypeValue.of("r2"),
                TypeValue.of("r3"),
                TypeValue.of("r4")
            )
        );
    }

    @Test
    public void testNestWhere2() {
        var expr = new WhereExpr();

        expr.where("r1", "r1")
            .where((query) -> {
                query.where("n1", "n1")
                     .orWhere("n2", "n2")
                 ;
            })
            .where((query) -> {
                query.where("d1", "d1").where("d2", "d2");
            })
            .orWhere(query -> {
                query.where("f1", "f1").orWhere("f2", "f2");
            })
            .orWhere(query -> {
                query.orWhere("p1", "p1").where("p2", "p2");
            })
            .where("r3", "r3")
            .orWhere("r2", "r2")
        ;

        G.assertEquals(
            mysqlGrammar,
            expr,
            "`r1` = ? AND (`n1` = ? OR `n2` = ?) AND (`d1` = ? AND `d2` = ?) OR (`f1` = ? OR `f2` = ?) OR (`p1` = ? AND `p2` = ?) AND `r3` = ? OR `r2` = ?",
            List.of(
                TypeValue.of("r1"),
                TypeValue.of("n1"),
                TypeValue.of("n2"),
                TypeValue.of("d1"),
                TypeValue.of("d2"),
                TypeValue.of("f1"),
                TypeValue.of("f2"),
                TypeValue.of("p1"),
                TypeValue.of("p2"),
                TypeValue.of("r3"),
                TypeValue.of("r2")
            )
        );
    }

    @Test
    public void testOn() {

        var expr = new WhereExpr();
        expr.on("user.id", "=", "order.user_id")
            .on("user.id", "=", "payment.user_id")
            .on("user.id", "=", "invoice.user_id")
            ;

        G.assertEquals(
            mysqlGrammar,
            expr,
            "`user`.`id` = `order`.`user_id` AND `user`.`id` = `payment`.`user_id` AND `user`.`id` = `invoice`.`user_id`"
        );
    }

    @Test
    public void testWhereExprWhereIn() {
        var expr = new WhereExpr();
        expr.where("id", 123).whereIn("id", List.of("id1", "id2")).whereIn("name", List.of("name1", "name2"));

        G.assertEquals(
            mysqlGrammar,
            expr,
            "`id` = ? AND `id` IN (?, ?) AND `name` IN (?, ?)",
            List.of(
                TypeValue.of(123),
                TypeValue.of("id1"),
                TypeValue.of("id2"),
                TypeValue.of("name1"),
                TypeValue.of("name2")
            )
        );
    }

    @Test
    public void testWhereExprWhereInQuery() {
        var expr = new WhereExpr();
        expr.where("id", 123)
            .whereIn("id",
                new Query().select("id")
                           .from("users")
                    .where(wrapper -> wrapper.where("id", ">", 10))
            )
            .orWhereIn("name",
                new Query().select("name")
                           .from("users")
                           .where(wrapper -> wrapper.where("id", ">", 10))
            )
        ;

        G.assertEquals(
            mysqlGrammar,
            expr,
            "`id` = ? AND `id` IN (SELECT `id` FROM `users` WHERE `id` > ?) OR `name` IN (SELECT `name` FROM `users` WHERE `id` > ?)",
            List.of(
                TypeValue.of(123),
                TypeValue.of(10),
                TypeValue.of(10)
            )
        );
    }

    @Test
    public void testWhereColumn() {
        var expr = new WhereExpr();

        expr.whereColumn("orders.user_id", "users.id");

        G.assertEquals(
            mysqlGrammar,
            expr,
            "`orders`.`user_id` = `users`.`id`"

        );
    }

    @Test
    public void testWhereExists() {
        // var orders = new SelectStmt();
        var orders = new Query()
            .selectRaw("1")
            .from("orders")
            .where((query) -> {
                query.whereColumn("orders.user_id", "users.id");
            })
            .toSelectStmt();

        var expr = new WhereExpr();
        expr.whereExists(orders);

        G.assertEquals(
            mysqlGrammar,
            expr,
            "EXISTS (SELECT 1 FROM `orders` WHERE `orders`.`user_id` = `users`.`id`)"

        );
    }

    @Test
    public void testTypes() {
        var expr = new WhereExpr();
        var date = Date.valueOf(LocalDate.now());
        var time = Time.valueOf(LocalTime.now());
        var timestamp = Timestamp.valueOf(LocalDateTime.now());
        var localDateTime = LocalDateTime.now();
        var bigDecimal = new BigDecimal("10000.999");
        expr
            .where("id", "1")
            .where("Integer", 1)
            .where("Long", 1L)
            .where("Float", 1.0F)
            .where("Double", 1.0D)
            .where("Date", date)
            .where("Time", time)
            .where("Timestamp", timestamp)
            .where("LocalDateTime", localDateTime)
            .where("BigDecimal", bigDecimal)
        ;

        G.assertEquals(
            mysqlGrammar,
            expr,
            "`id` = ? AND `Integer` = ? AND `Long` = ? AND `Float` = ? AND `Double` = ? AND `Date` = ? AND `Time` = ? AND `Timestamp` = ? AND `LocalDateTime` = ? AND `BigDecimal` = ?",
            List.of(
                TypeValue.of("1"),
                TypeValue.of(1),
                TypeValue.of(1L),
                TypeValue.of(1.0F),
                TypeValue.of(1.0D),
                TypeValue.of(date),
                TypeValue.of(time),
                TypeValue.of(timestamp),
                TypeValue.of(localDateTime),
                TypeValue.of(bigDecimal)
            )

        );
    }

    @Test
    public void testNull() {
        var expr = new WhereExpr();
        expr.whereNull("deleted_at")
            .whereNotNull("deleted_at")
            .orWhereNull("deleted_at")
            .orWhereNotNull("deleted_at")
            ;

        G.assertEquals(
            mysqlGrammar,
            expr,
            "`deleted_at` IS NULL AND `deleted_at` IS NOT NULL OR `deleted_at` IS NULL OR `deleted_at` IS NOT NULL"

        );
    }

    @Test
    public void testWhereIn() {
        var expr = new WhereExpr();
        expr
            .whereIn("id", "1", "2", "3")
            .whereIn("Integer", 1, 2, 3)
            .whereIn("Long", 1L, 2L, 3L)
            .whereIn("Float", 1.0F, 2.0F, 3.0F)
            .whereIn("Double", 1.0D, 2.0D, 3.0D)
            .whereIn("Date", Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()))
            .whereIn("Time", Time.valueOf(LocalTime.now()), Time.valueOf(LocalTime.now()))
            .whereIn("Timestamp", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()))
            .whereIn("LocalDateTime", LocalDateTime.now(), LocalDateTime.now())
            .whereIn("BigDecimal", new BigDecimal("8888.888"), new BigDecimal("9999.99"))
        ;

        // exprAssert(expr);


    }

    @Test
    public void testWhereNotIn() {
        var expr = new WhereExpr();
        expr
            .orWhereIn("id", "1", "2", "3")
            .orWhereIn("Integer", 1, 2, 3)
            .orWhereIn("Long", 1L, 2L, 3L)
            .orWhereIn("Float", 1.0F, 2.0F, 3.0F)
            .orWhereIn("Double", 1.0D, 2.0D, 3.0D)
            .orWhereIn("Date", Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()))
            .orWhereIn("Time", Time.valueOf(LocalTime.now()), Time.valueOf(LocalTime.now()))
            .orWhereIn("Timestamp", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()))
            .orWhereIn("LocalDateTime", LocalDateTime.now(), LocalDateTime.now())
            .orWhereIn("BigDecimal", new BigDecimal("8888.888"), new BigDecimal("9999.99"))
        ;

        // exprAssert(expr);

    }

    @Test
    public void testWhereRaw() {
        var expr = new WhereExpr();
        expr
            .whereRaw("id=? and name=?", List.of(TypeValue.of("1"), TypeValue.of("name")))
        ;

        G.assertEquals(
            mysqlGrammar,
            expr,
            "id=? and name=?",
            List.of(
                TypeValue.of("1"),
                TypeValue.of("name")
            )

        );

    }


}
