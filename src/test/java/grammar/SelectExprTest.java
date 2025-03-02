package test.java.grammar;

import com.lyhux.sqlbuilder.grammar.EscapedStr;
import com.lyhux.sqlbuilder.grammar.RawStr;
import com.lyhux.sqlbuilder.grammar.SelectExpr;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class SelectExprTest extends MysqlTest {

    @Test
    public void testSelectExpr() {
        var expr = new SelectExpr();
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
