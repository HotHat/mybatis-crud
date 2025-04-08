package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.EscapedStr;
import com.lyhux.mybatiscrud.builder.grammar.LimitExpr;
import com.lyhux.mybatiscrud.builder.grammar.OrderByExpr;
import com.lyhux.mybatiscrud.builder.grammar.OrderByItem;
import com.lyhux.mybatiscrud.builder.grammar.select.ForExpr;
import com.lyhux.mybatiscrud.builder.vendor.Grammar;
import com.lyhux.mybatiscrud.builder.vendor.MysqlGrammar;
import org.junit.jupiter.api.Test;

import java.util.List;

public class OrderByLimitForTest {
    static final Grammar mysqlGrammar = new MysqlGrammar();

    @Test
    public void testOrderBy() {
        var orderBy = new OrderByExpr();
        orderBy.add(new OrderByItem(new EscapedStr("id"), "desc"))
                .add(new OrderByItem(new EscapedStr("name"), "asc"))
                ;

        G.assertEquals(
            mysqlGrammar,
            orderBy,
            "`id` DESC, `name` ASC",
            List.of()
        );
    }

    @Test
    public void testLimit() {
        var limit = new LimitExpr(10);

        G.assertEquals(
            mysqlGrammar,
            limit,
            "LIMIT 10"
        );

        var limit2 = new LimitExpr(10, 5);
        G.assertEquals(
            mysqlGrammar,
            limit2,
            "LIMIT 10 OFFSET 5"
        );
    }

    @Test
    public void testFor() {
        var forExpr = new ForExpr("update");
        forExpr.of("users", "orders");
        G.assertEquals(
            mysqlGrammar,
            forExpr,
            "FOR UPDATE OF `users`, `orders`"
        );

        var forExpr1 = new ForExpr("update");
        G.assertEquals(
            mysqlGrammar,
            forExpr1,
            "FOR UPDATE"
        );
    }
}
