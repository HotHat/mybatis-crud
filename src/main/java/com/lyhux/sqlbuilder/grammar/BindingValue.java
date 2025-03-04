package com.lyhux.sqlbuilder.grammar;

import java.util.ArrayList;
import java.util.List;

public record BindingValue<T>(ExprStr expr, SelectStmt stmt, List<TypeValue<T>> bindings) {

    public BindingValue(ExprStr expr) {
        this(expr, null, new ArrayList<TypeValue<T>>());
    }

    public BindingValue(ExprStr expr, TypeValue<T> binding) {
        this(expr, null, new ArrayList<TypeValue<T>>());
        this.bindings.add(binding);
    }

    public BindingValue(ExprStr expr, List<TypeValue<T>> bindings) {
        this(expr, null, bindings);
    }

    public BindingValue(SelectStmt stmt) {
        this(null, stmt, null);
    }

    public boolean isExpr() { return this.expr != null; }
    public boolean isStmt() { return this.stmt != null; }

    @Override
    public String toString() {
        if (this.isExpr()) {
            return "[expr=" + expr.getValue() + ", bindings=" + (bindings == null ? "null" : bindings.toString()) + "]";
        } else {
            return stmt.toString();
        }
    }
}
