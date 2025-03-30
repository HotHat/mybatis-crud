package com.lyhux.mybatiscrud.builder.grammar;

public  record ValueExprX<T>(ExprStr expr, TypeValue<T> binding) {
    public ValueExprX(ExprStr expr) {
        this(expr, null);
    }

    @Override
    public String toString() {
        return "[expr=" + expr.getValue() + ", bindings=" + (binding == null ? "null" : binding.toString()) + "]";
    }
}
