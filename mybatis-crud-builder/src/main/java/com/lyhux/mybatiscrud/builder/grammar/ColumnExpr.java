package com.lyhux.mybatiscrud.builder.grammar;

import java.util.ArrayList;
import java.util.List;

public class ColumnExpr implements Expr {
    List<ExprStr> select;

    public List<ExprStr> getSelect() {
        return select;
    }

    public boolean isEmpty() { return select.isEmpty(); }

    public ColumnExpr add(ExprStr expr) {
        select.add(expr);
        return this;
    }

    public void addAll(List<? extends ExprStr> fields) {
        this.select.addAll(fields);
    }

    public ColumnExpr() {
        select = new ArrayList<ExprStr>();
    }

    public ColumnExpr(List<ExprStr> select) {
        this.select = select;
    }
}
