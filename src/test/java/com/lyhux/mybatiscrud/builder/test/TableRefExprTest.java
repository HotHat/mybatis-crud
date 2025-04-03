package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.*;
import org.junit.jupiter.api.Test;

public class TableRefExprTest extends MysqlGrammarTest {



    @Test
    public void testTableNameExpr() {
        var table = new TableNameExpr(new EscapedStr("users"));
        exprAssert(table);

        var tableAlias = new TableNameExpr(new EscapedStr("users"), new EscapedStr("alias"));
        exprAssert(tableAlias);
    }

    @Test
    public void testTableSubExpr() {
//        var sub = new TableSubExpr();
    }

    @Test
    public void testTableJoinExpr() {
        var table = new TableNameExpr(new EscapedStr("users"));
        var where = new WhereExpr(false);
        where.on("users.id", "=", "orders.user_id").where("users.id", ">", 13);
        var joinedExpr = new TableJoinedExpr(table, where);

        exprAssert(joinedExpr);
    }

    @Test
    public void testTableRefExpr() {
        var tableRef = new TableRefExpr(new TableNameExpr("users"));
        exprAssert(tableRef);

        var tableRef2 = new TableRefExpr(new TableNameExpr("users"));
        exprAssert(tableRef2);

        var table1 = new TableNameExpr("orders");
        var where1 = new WhereExpr(false);
        where1.on("users.id", "=", "orders.user_id");
        var joined1 = new TableJoinedExpr(table1, where1);

        var table2 = new TableNameExpr("items");
        var where2 = new WhereExpr(false);
        where2.on("orders.id", "=", "items.order_id");
        var joined2 = new TableJoinedExpr(table2, where2);

        tableRef2.add(joined1);
        tableRef2.add(joined2);
        exprAssert(tableRef2);
    }

    @Test
    public void testTableRefsExpr() {
        var tableRef = new TableRefExpr(new TableNameExpr("users"));
        exprAssert(tableRef);

        var tableRef2 = new TableRefExpr(new TableNameExpr("orders"));
        exprAssert(tableRef2);

        var refs = new TableRefsExpr();
        refs.add(tableRef);
        refs.add(tableRef2);


        exprAssert(refs);
    }

}
