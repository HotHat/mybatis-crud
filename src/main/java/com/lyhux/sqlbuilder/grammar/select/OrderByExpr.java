package com.lyhux.sqlbuilder.grammar.select;

import com.lyhux.sqlbuilder.grammar.Expr;

import java.util.ArrayList;
import java.util.List;

public final class OrderByExpr implements Expr {
    List<OrderByItem> items;

    public OrderByExpr() {
        items = new ArrayList<OrderByItem>();
    }

    public OrderByExpr add(OrderByItem item) {
        items.add(item);
        return this;
    }

    public List<OrderByItem> getItems() { return items; }
}
