package com.lyhux.sqlbuilder.grammar;

public  record ValueExpr<T>(ExprStr expr, ExprValue<T> binding) {
    public ValueExpr(ExprStr expr) {
        this(expr, null);
    }

    @Override
    public String toString() {
        return "[expr=" + expr.getValue() + ", binding=" + (binding == null ? "null" : binding.toString()) + "]";
    }
}
