package com.lyhux.mybatiscrud.builder.grammar;

import java.util.ArrayList;
import java.util.List;

public final class OrderByExpr implements Expr {
    List<OrderByItem> items;

    public OrderByExpr() {
        items = new ArrayList<OrderByItem>();
    }

    public boolean isEmpty() { return items.isEmpty(); }

    public OrderByExpr add(OrderByItem item) {
        items.add(item);
        return this;
    }

    public List<OrderByItem> getItems() { return items; }
}
