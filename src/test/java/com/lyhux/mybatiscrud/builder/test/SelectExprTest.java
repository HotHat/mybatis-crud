package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.*;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SelectExprTest extends MysqlGrammarTest {

    @Test
    public void testEmptySelectExpr() {
        var expr = new ColumnExpr();

        exprAssert(
            expr,
            new ExprResult(
                "*",
                List.of()
            )
        );
    }

    @Test
    public void testSelectExpr() {
        var expr = new ColumnExpr();
        expr.add(new RawStr("version()"))
                .add(new EscapedStr("id AS user_id"))
                .add(new EscapedStr("orders.id AS order_id"))
        ;

        exprAssert(expr, new ExprResult(
            "version(), `id` AS `user_id`, `orders`.`id` AS `order_id`",
            List.of()
        ));
    }

    @Test
    public void testSelectRaw() {

        var expr = new ColumnExpr();
        expr.add(new RawStr("price * ? as price_with_tax"), TypeValue.of("1.0825"));

        exprAssert(
            expr,
            new ExprResult(
                "price * ? as price_with_tax",
                List.of( TypeValue.of("1.0825"))
            )
        );
    }

    @Test
    public void testStringSpilt() {
        var sb = new StringBuilder();

        var field = "user.id AS user_id";
        var field1 = "user.id";
        var field2 = "id";

        System.out.printf("%s: %s\n", field, escape(field));
        System.out.printf("%s: %s\n", field1, escape(field1));
        System.out.printf("%s: %s\n", field2, escape(field2));
    }

    private String escape(String field) {
        var sb = new StringBuilder();
        String[] asSplit = field.split("\\s+(as|AS)\\s+");

        int outCount = 0;
        for (String s : asSplit) {
            var dotSplit = s.split("\\.");

            int innerCount = 0;
            for (String d : dotSplit) {
                sb.append('`').append(d).append('`');
                if (++innerCount < dotSplit.length) {
                    sb.append(".");
                }
            }

            if (++outCount < asSplit.length) {
                sb.append(" AS ");
            }
        }

        return sb.toString();
    }


}
