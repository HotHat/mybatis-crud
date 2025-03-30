package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.EscapedStr;
import com.lyhux.mybatiscrud.builder.grammar.LimitExpr;
import com.lyhux.mybatiscrud.builder.grammar.OrderByExpr;
import com.lyhux.mybatiscrud.builder.grammar.OrderByItem;
import com.lyhux.mybatiscrud.builder.grammar.select.ForExpr;
import org.junit.jupiter.api.Test;

public class OrderByLimitForTest extends MysqlGrammarTest {
    @Test
    public void testOrderBy() {
        var orderBy = new OrderByExpr();
        orderBy.add(new OrderByItem(new EscapedStr("id"), "desc"))
                .add(new OrderByItem(new EscapedStr("name"), "asc"))
                ;

        print(orderBy);
    }

    @Test
    public void testLimit() {
        var limit = new LimitExpr(10);
        print(limit );

        var limit2 = new LimitExpr(10, 5);
        print(limit2);
    }

    @Test
    public void testFor() {
        var forExpr = new ForExpr("update");
        forExpr.of("users", "orders");
        print(forExpr);

        var forExpr1 = new ForExpr("update");
        print(forExpr1);
    }
}
