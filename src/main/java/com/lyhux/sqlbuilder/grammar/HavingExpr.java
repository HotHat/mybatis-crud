package com.lyhux.sqlbuilder.grammar;

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
