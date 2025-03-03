package com.lyhux.sqlbuilder.grammar.select;

import com.lyhux.sqlbuilder.grammar.Expr;
import com.lyhux.sqlbuilder.grammar.ExprStr;

import java.util.ArrayList;
import java.util.List;

public class SelectExpr implements Expr {
    List<ExprStr> select;

    public List<ExprStr> getSelect() {
        return select;
    }

    public boolean isEmpty() { return select.isEmpty(); }

    public SelectExpr add(ExprStr expr) {
        select.add(expr);
        return this;
    }

    public void addAll(List<? extends ExprStr> fields) {
        this.select.addAll(fields);
    }

    public SelectExpr() {
        select = new ArrayList<ExprStr>();
    }

    public SelectExpr(List<ExprStr> select) {
        this.select = select;
    }
}
