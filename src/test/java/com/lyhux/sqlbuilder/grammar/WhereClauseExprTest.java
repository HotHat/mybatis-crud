package com.lyhux.sqlbuilder.grammar;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class WhereClauseExprTest extends MysqlGrammarTest {

    @Test
    public void testWhereClauseExpr() {
        var expr = new WhereExpr(true);
        expr.add(new BinaryExpr(
            new EscapedStr("id"),
            "=",
            new BindingValue<>(new RawStr("?"),
                List.of(new TypeValue<>(JDBCType.INTEGER, 123)))
        ), "AND");

        expr.where("id", "123");

        var result = grammar.compile(expr);
        print(result);
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

        var result = grammar.compile(expr);
        print(result);
    }

    @Test
    public void testNestWhere1() {
        var expr = new WhereExpr();

        expr.where("r1", "r1")
            .orWhere("r2", "r2")
            .where("r3", "r3")
            .orWhere("r4", "r4")
        ;

        var result = grammar.compile(expr);
        print(result);
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
                query.orWhere("p1", "p1").orWhere("p2", "p2");
            })
            .where("r3", "r3")
            .orWhere("r2", "r2")
        ;

        var result = grammar.compile(expr);
        print(result);
    }

    @Test
    public void testOn() {

        var expr = new WhereExpr();
        expr.on("user.id", "=", "order.user_id")
            .on("user.id", "=", "payment.user_id")
            .on("user.id", "=", "invoice.user_id")
            ;

        var result = grammar.compile(expr);
        print(result);
    }

    @Test
    public void testWhereExprWhereIn() {
        var expr = new WhereExpr();
        expr.where("id", 123).whereIn("id", List.of("id1", "id2")).whereIn("name", List.of("name1", "name2"));

        print(expr);
    }

    @Test
    public void testWhereColumn() {
        var expr = new WhereExpr();

        expr.whereColumn("orders.user_id", "users.id");

        print(expr);
    }

    @Test
    public void testWhereExists() {
        var orders = new SelectStmt();
        orders.selectRaw("1")
              .from("orders")
              .where((query) -> {
                  query.whereColumn("orders.user_id", "users.id");
              });

        var expr = new WhereExpr();
        expr.whereExists(orders);

        print(expr);
    }

    @Test
    public void testTypes() {
        var expr = new WhereExpr();
        expr
            .where("id", "1")
            .where("Integer", 1)
            .where("Long", 1L)
            .where("Float", 1.0F)
            .where("Double", 1.0D)
            .where("Date", Date.valueOf(LocalDate.now()))
            .where("Time", Time.valueOf(LocalTime.now()))
            .where("Timestamp", Timestamp.valueOf(LocalDateTime.now()))
            .where("LocalDateTime", LocalDateTime.now())
            .where("BigDecimal", new BigDecimal("10000.999"))
        ;

        print(expr);
    }

    @Test
    public void testNull() {
        var expr = new WhereExpr();
        expr.whereNull("deleted_at")
            .whereNotNull("deleted_at")
            .orWhereNull("deleted_at")
            .orWhereNotNull("deleted_at")
            ;
        print(expr);
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

        print(expr);


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

        print(expr);

    }


}
