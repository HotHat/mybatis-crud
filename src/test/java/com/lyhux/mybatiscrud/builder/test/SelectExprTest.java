package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.EscapedStr;
import com.lyhux.mybatiscrud.builder.grammar.RawStr;
import com.lyhux.mybatiscrud.builder.grammar.ColumnExpr;
import org.junit.jupiter.api.Test;

public class SelectExprTest extends MysqlGrammarTest {

    @Test
    public void testEmptySelectExpr() {
        var expr = new ColumnExpr();

        print(expr);
    }

    @Test
    public void testSelectExpr() {
        var expr = new ColumnExpr();
        expr.add(new RawStr("version()"))
                .add(new EscapedStr("id AS user_id"))
                .add(new EscapedStr("orders.id AS order_id"))
        ;

        print(expr);
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
