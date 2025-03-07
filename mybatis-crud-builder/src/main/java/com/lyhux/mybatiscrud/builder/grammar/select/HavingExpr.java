package com.lyhux.mybatiscrud.builder.grammar.select;

import com.lyhux.mybatiscrud.builder.grammar.Expr;
import com.lyhux.mybatiscrud.builder.grammar.WhereExpr;

public final class HavingExpr implements Expr {
    private final WhereExpr expr;

    public HavingExpr() {
        expr = new WhereExpr();
    }

    public WhereExpr getExpr() {
        return expr;
    }

    public boolean isEmpty() { return expr.isEmpty(); }

}
