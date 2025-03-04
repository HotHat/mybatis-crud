package com.lyhux.sqlbuilder.grammar;

import org.junit.jupiter.api.Test;

import java.sql.JDBCType;
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
    public void testWhereExprWhereIn() {
        var expr = new WhereExpr();
        expr.where("id", 123).whereIn("id", List.of("id1", "id2")).whereIn("name", List.of("name1", "name2"));

        print(expr);
    }

    @Test
    public void testWhereColumn() {
        var expr = new WhereExpr();

        expr.whereColumn("orders.user_id",  "users.id");

        print(expr);
    }

    @Test
    public void testWhereExists() {
        var orders = new SelectStmt();
        orders.selectRaw("1")
                .from("orders")
                .where((query) -> {
                    query.whereColumn("orders.user_id",  "users.id");
                });

        var expr = new WhereExpr();
        expr.whereExists(orders);

        print(expr);
    }


}
