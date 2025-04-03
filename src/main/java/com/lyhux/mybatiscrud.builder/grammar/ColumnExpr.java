package com.lyhux.mybatiscrud.builder.grammar;

import java.util.ArrayList;
import java.util.List;

public class ColumnExpr implements Expr {
    List<ExprStr> select;
    List<TypeValue<?>> bindings;

    public List<ExprStr> getSelect() { return select; }
    public List<TypeValue<?>> getBindings() { return bindings; }

    public boolean isEmpty() { return select.isEmpty(); }

    public ColumnExpr add(ExprStr expr) {
        select.add(expr);
        return this;
    }

    public ColumnExpr add(ExprStr expr, TypeValue<?> binding) {
        select.add(expr);
        bindings.add(binding);
        return this;
    }

    public ColumnExpr add(ExprStr expr, List<TypeValue<?>> bindings) {
        select.add(expr);
        this.bindings.addAll(bindings);
        return this;
    }

    public void addAll(List<? extends ExprStr> fields) {
        this.select.addAll(fields);
    }

    public ColumnExpr() {
        select = new ArrayList<ExprStr>();
        bindings = new ArrayList<>();
    }

    public ColumnExpr(List<ExprStr> select) {
        this.select = select;
    }
}
